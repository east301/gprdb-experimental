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

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "entity")
class IdentifierInfoImpl implements IdentifierInfo {

    private final IdentifierInternalSharedData sharedData;
    private final IdentifierInfoEntity entity;

    @Override
    public IdentifierDataset getDataset() {
        return this.sharedData.getIdentifierDatasetMap().get(this.entity.getDatasetId());
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(this.entity.getName());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.entity.getDescription());
    }

    @Override
    public Collection<IdentifierMapping> getMappings() {
        return Lists.transform(this.entity.getMappings(), m -> new IdentifierMappingImpl(this.sharedData, m));
    }

}
