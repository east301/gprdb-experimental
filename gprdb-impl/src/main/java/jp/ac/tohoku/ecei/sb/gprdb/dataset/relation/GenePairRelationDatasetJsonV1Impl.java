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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class GenePairRelationDatasetJsonV1Impl implements GenePairRelationDatasetJsonV1 {

    @JsonIgnore
    private final GenePairRelationDataset parent;

    @Override
    public String getId() {
        return this.parent.getId();
    }

    @Override
    public String getName() {
        return this.parent.getName();
    }

    @Override
    public String getShortName() {
        return this.parent.getShortName();
    }

    @Override
    public String getVersion() {
        return this.parent.getVersion();
    }

    @Override
    public List<String> getRelationTypes() {
        return Lists.transform(this.parent.getRelationTypes(), GenePairRelationType::getId);
    }

    @Override
    public String getSpecies() {
        return this.parent.getSpecies().getId();
    }

    @Override
    public int getNumGenes() {
        return this.parent.getNumGenes();
    }

}
