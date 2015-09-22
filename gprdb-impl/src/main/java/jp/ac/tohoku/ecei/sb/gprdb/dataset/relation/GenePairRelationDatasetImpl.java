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
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Shu Tadaka
 */
@Slf4j
class GenePairRelationDatasetImpl
        extends AbstractIdentifiableImpl<GenePairRelationConfig.DatasetConfig>
        implements GenePairRelationDataset {

    private final IdentifierManager identifierManager;
    private final GenePairRelationManager manager;
    private final GenePairRelationConfig.RdfVocabularyConfig rdfVocabularyConfig;

    public GenePairRelationDatasetImpl(
        @NotNull IdentifierManager identifierManager,
        @NotNull GenePairRelationManager manager,
        @NotNull GenePairRelationConfig.DatasetConfig datasetConfig,
        @NotNull GenePairRelationConfig.RdfVocabularyConfig rdfVocabularyConfig) {

        super(datasetConfig);

        this.identifierManager = identifierManager;
        this.manager = manager;
        this.rdfVocabularyConfig = rdfVocabularyConfig;
    }

    @Override
    public String getVersion() {
        return this.config.getVersion();
    }

    @Override
    public List<GenePairRelationType> getRelationTypes() {
        Map<String, GenePairRelationType> typeIdMap = Maps.uniqueIndex(
                this.manager.getTypes(), GenePairRelationType::getId);

        return Lists.transform(this.config.getRelationTypes(), typeIdMap::get);
    }

    @Override
    public Identifier getSpecies() {
        return this.identifierManager.wrap(this.config.getSpecies());
    }

    @Override
    public boolean isHidden() {
        return this.config.isHidden();
    }

    @Override
    public List<Identifier> getGenes() {
        try (GenePairRelationReader reader = newReader()) {
            return reader.getGenes();
        } catch (Exception ex) {
            log.warn("Failed to create gene-pair relation reader", ex);
            return Collections.emptyList();
        }
    }

    @Override
    public int getNumGenes() {
        return getGenes().size();
    }

    @Override
    public String getLoaderFactoryClass() {
        return this.config.getLoaderFactoryClass();
    }

    @Override
    public Map<String, Object> getLoaderConfig() {
        return this.config.getLoaderConfig();
    }

    @Override
    public GenePairRelationReader newReader() {
        return this.manager.getAccessorFactory().newReader(this.config.getId());
    }

}
