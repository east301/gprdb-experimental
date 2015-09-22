/**
 * gprdb - a simple and flexible framework for gene-pair relation database construction
 * Copyright 2015 gprdb developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jp.ac.tohoku.ecei.sb.gprdb.dataset.relation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import jp.ac.tohoku.ecei.sb.gprdb.app.CliCommand;
import jp.ac.tohoku.ecei.sb.gprdb.io.LoaderManager;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An implementation of {@link CliCommand} to load gene-pair relation data into internal datastore.
 *
 * @author Shu Tadaka
 */
@Component
@Slf4j
class GenePairRelationLoadCommandImpl implements CliCommand {

    private static final String TASK_ID = "load-gene-pair-relation";
    private static final String TARGET_KEY = "target";

    @Override
    public Optional<String> register(@NotNull Subparsers subparsers) {
        //
        Subparser parser = subparsers.addParser(TASK_ID);
        parser.addArgument(TARGET_KEY);

        //
        return Optional.of(TASK_ID);
    }

    @Override
    public void execute(
            @NotNull Namespace arguments, @NotNull ApplicationContext applicationContext)
            throws Exception {

        //
        GenePairRelationManager genePairRelationManager = applicationContext.getBean(GenePairRelationManager.class);
        GenePairRelationDataset dataset = genePairRelationManager.getDataset(arguments.getString(TARGET_KEY));
        GenePairRelationLoader loader = applicationContext.getBean(LoaderManager.class).newLoader(dataset);

        //
        List<String> geneIds = Lists.newArrayList();
        loader.loadGenes(geneIds::add);
        geneIds.sort(Comparator.naturalOrder());

        log.info("{} genes found, starting to write gene-pair relation strength matrix", geneIds.size());

        //
        IdentifierManager identifierManager = applicationContext.getBean(IdentifierManager.class);
        List<Identifier> genes = Lists.transform(geneIds, identifierManager::wrap);

        //
        Map<String, GenePairRelationType> typeIdMap
                = Maps.uniqueIndex(dataset.getRelationTypes(), GenePairRelationType::getId);
        GenePairRelationAccessorFactory accessorFactory = genePairRelationManager.getAccessorFactory();

        try (GenePairRelationWriter writer
                 = accessorFactory.newWriter(dataset.getId(), dataset.getRelationTypes(), genes)) {

            //
            Set<String> processedGeneIds = Sets.newHashSet();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            double start = System.currentTimeMillis();

            //
            loader.loadRelations(r -> {
                //
                GenePairRelationType type = typeIdMap.get(r.getRelationType());
                Identifier guideGene = identifierManager.wrap(r.getGuideGene());
                Map<Identifier, Double> relations = convertRelationMap(identifierManager, r.getRelationStrengthMap());

                //
                writer.writeMap(type, guideGene, relations);

                //
                if (processedGeneIds.add(r.getGuideGene()) && processedGeneIds.size() % 1000 == 0) {
                    int count = processedGeneIds.size();
                    double elapsedMSeconds = System.currentTimeMillis() - start;
                    double remainingMSeconds = (geneIds.size() - count) / (count / elapsedMSeconds);
                    Date estimatedFinishDateTime = new Date(System.currentTimeMillis() + (long)remainingMSeconds);

                    log.info(
                        "{} genes written in {} minutes. estimated finish datetime is {}",
                        count, (elapsedMSeconds / 1000 / 60), dateFormat.format(estimatedFinishDateTime));
                }
            });
        }
    }

    private static Map<Identifier, Double> convertRelationMap(
            @NotNull IdentifierManager identifierManager, @NotNull Map<String, Double> source) {

        Map<Identifier, Double> result = Maps.newHashMap();
        for (Map.Entry<String, Double> entry : source.entrySet()) {
            result.put(identifierManager.wrap(entry.getKey()), entry.getValue());
        }

        return result;
    }

}
