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

package jp.ac.tohoku.ecei.sb.gprdb.dataset.id;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import jp.ac.tohoku.ecei.sb.gprdb.app.CliCommand;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStore;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 * @author Shu Tadaka
 */
@Component
class IdentifierDatastoreOptimizationCommandImpl implements CliCommand {

    private static final String COMMAND = "optimize-identifier-datastore";

    @Override
    public Optional<String> register(@NotNull Subparsers subparsers) {
        subparsers.addParser(COMMAND);
        return Optional.of(COMMAND);
    }

    @Override
    @Transactional
    public void execute(
        @NotNull Namespace arguments, @NotNull ApplicationContext applicationContext)
        throws Exception {

        //
        IdentifierInternalSharedData sharedData = applicationContext.getBean(IdentifierInternalSharedData.class);

        //
        createNameAndDescriptionCaches(sharedData);
        createIntraSpeciesIdentifierMappingCache(sharedData);
        createInterSpeciesIdentifierMappingCache(sharedData);
        sharedData.getIdentifierEntityFullTextSearchService().rebuildIndexes();
    }

    private static void createNameAndDescriptionCaches(@NotNull IdentifierInternalSharedData sharedData) {
        //
        Set<String> ids = Sets.newHashSet();
        KeyValueStore<String, String> nameCache = sharedData.getIdentifierNameCache();
        KeyValueStore<String, String> descriptionCache = sharedData.getIdentifierDescriptionCache();

        //
        nameCache.clear();
        descriptionCache.clear();

        //
        Sort sort = new Sort(IdentifierInfoEntity.FIELD_IDENTIFIER, IdentifierInfoEntity.FIELD_PRIORITY);
        for (IdentifierInfoEntity entity : sharedData.getIdentifierInfoEntityRepository().findAll(sort)) {
            //
            String id = entity.getIdentifier();
            String name = entity.getName();
            String description = entity.getDescription();

            //
            if (!ids.contains(id) && (name != null || description != null)) {
                ids.add(id);
                if (name != null) {
                    nameCache.put(id, name);
                }
                if (description != null) {
                    descriptionCache.put(id, description);
                }
            }
        }

        //
        nameCache.commit();
        descriptionCache.commit();

        nameCache.optimize();
        descriptionCache.optimize();
    }

    private static void createIntraSpeciesIdentifierMappingCache(
        @NotNull IdentifierInternalSharedData sharedData) {

        //
        Multimap<String, String> mappings = HashMultimap.create();

        for (IdentifierMappingEntity entity : sharedData.getIdentifierMappingEntityRepository().findAll()) {
            if (entity.getType().equals(IdentifierMappingTypes.INTRA_SPECIES)) {
                String key = entity.getIdentifierInfo().getIdentifier() + "/*";
                mappings.put(key, entity.getTargetIdentifier());
            }
        }

        //
        KeyValueStore<String, String> cache = sharedData.getIdentifierIntraSpeciesMappingCache();
        cache.clear();

        for (String key : mappings.keySet()) {
            cache.put(key, Joiner.on(";").join(mappings.get(key)));
        }

        //
        cache.commit();
        cache.optimize();
    }

    private static void createInterSpeciesIdentifierMappingCache(
        @NotNull IdentifierInternalSharedData sharedData) {

        //
        Multimap<String, String> mappings = HashMultimap.create();

        for (IdentifierMappingEntity entity : sharedData.getIdentifierMappingEntityRepository().findAll()) {
            if (entity.getType().equals(IdentifierMappingTypes.INTER_SPECIES)) {
                String key = entity.getIdentifierInfo().getIdentifier() + "/" + entity.getHint();
                mappings.put(key, entity.getTargetIdentifier());
            }
        }

        //
        KeyValueStore<String, String> cache = sharedData.getIdentifierInterSpeciesMappingCache();
        cache.clear();

        for (String key : mappings.keySet()) {
            cache.put(key, Joiner.on(";").join(mappings.get(key)));
        }

        //
        cache.commit();
        cache.optimize();
    }

}
