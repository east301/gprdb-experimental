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
import jp.ac.tohoku.ecei.sb.gprdb.dataset.DatasetObjectNotFoundException;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Shu Tadaka
 */
@Component
class GenePairRelationManagerImpl implements GenePairRelationManager {

    private final IdentifierManager identifierManager;
    private final GenePairRelationConfig rootConfig;
    private final GenePairRelationAccessorFactory accessorFactory;
    private final GenePairRelationTableFactory tableFactory;
    private final Map<String, GenePairRelationStrengthFormatter> formatterMap;

    @Autowired
    public GenePairRelationManagerImpl(
        @NotNull IdentifierManager identifierManager,
        @NotNull GenePairRelationConfig rootConfig,
        @NotNull GenePairRelationAccessorFactory accessorFactory,
        @NotNull GenePairRelationTableFactory tableFactory,
        @NotNull List<GenePairRelationStrengthFormatter> relationStrengthFormatters) {

        this.identifierManager = identifierManager;
        this.rootConfig = rootConfig;
        this.accessorFactory = accessorFactory;
        this.tableFactory = tableFactory;
        this.formatterMap = Maps.uniqueIndex(relationStrengthFormatters, f -> f.getClass().getName());
    }

    @Override
    public List<GenePairRelationType> getTypes() {
        return Lists.transform(
            this.rootConfig.getTypes(),
            c -> new GenePairRelationTypeImpl(c, this.rootConfig, this.formatterMap));
    }

    @Override
    public GenePairRelationType getType(@NotEmpty String id) {
        for (GenePairRelationConfig.TypeConfig config : this.rootConfig.getTypes()) {
            if (config.getId().equals(id)) {
                return new GenePairRelationTypeImpl(config, this.rootConfig, this.formatterMap);
            }
        }

        throw DatasetObjectNotFoundException.genePairRelationTypeNotFound(id);
    }

    @Override
    public List<GenePairRelationDataset> getDatasets() {
        return getDatasets(false);
    }

    @Override
    public List<GenePairRelationDataset> getDatasets(boolean includeHidden) {
        return this.rootConfig
            .getDatasets()
            .stream()
            .filter(d -> !d.isHidden() || includeHidden)
            .map(d -> new GenePairRelationDatasetImpl(
                this.identifierManager, this, d, this.rootConfig.getRdfVocabularies()))
            .collect(Collectors.toList());
    }

    @Override
    public GenePairRelationDataset getDataset(@NotEmpty String id) {
        for (GenePairRelationConfig.DatasetConfig config : this.rootConfig.getDatasets()) {
            if (config.getId().equals(id)) {
                return new GenePairRelationDatasetImpl(
                    this.identifierManager,
                    this,
                    config,
                    this.rootConfig.getRdfVocabularies());
            }
        }

        throw DatasetObjectNotFoundException.genePairRelationDatasetNotFound(id);
    }

    @Override
    public GenePairRelationAccessorFactory getAccessorFactory() {
        return this.accessorFactory;
    }

    @Override
    public GenePairRelationTable createTable(
        @NotNull GenePairRelationDataset guideDataset,
        @NotNull Identifier guideGene,
        @NotNull GenePairRelationType relationType,
        @Min(0) int start,
        @Min(1) int count) {

        return this.tableFactory.create(getDatasets(true), guideDataset, relationType, guideGene, start, count);
    }

}
