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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.DatasetObjectNotFoundException;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Shu Tadaka
 */
@Component
class IdentifierManagerImpl implements IdentifierManager {

    private final IdentifierInternalSharedData sharedData;

    @Autowired
    public IdentifierManagerImpl(@NotNull IdentifierInternalSharedData sharedData) {
        this.sharedData = sharedData;
    }

    @Override
    public List<IdentifierDataset> getDatasets() {
        return Lists.transform(
            this.sharedData.getRootConfig().getDatasets(),
            IdentifierDatasetImpl::new);
    }

    @Override
    public IdentifierDataset getDataset(@NotEmpty String id) {
        for (IdentifierConfig.DatasetConfig config : this.sharedData.getRootConfig().getDatasets()) {
            if (config.getId().equals(id)) {
                return new IdentifierDatasetImpl(config);
            }
        }

        throw DatasetObjectNotFoundException.identifierDatasetNotFound(id);
    }

    @Override
    public ListMultimap<String, IdentifierLinkResolver> getLinkResolvers() {
        return this.sharedData.getIdentifierLinkGeneratorMap();
    }

    @Override
    public Identifier wrap(@NotEmpty String id) {
        return new IdentifierImpl(this.sharedData, id);
    }

    @Override
    public IdentifierImporter newImporter(@NotNull IdentifierDataset dataset) {
        return new IdentifierImporterImpl(this.sharedData, dataset);
    }

}
