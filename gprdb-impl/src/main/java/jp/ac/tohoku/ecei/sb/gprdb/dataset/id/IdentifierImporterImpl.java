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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
@Slf4j
class IdentifierImporterImpl implements IdentifierImporter {

    private final IdentifierInternalSharedData sharedData;
    private final IdentifierDataset dataset;

    private final List<RawIdentifierInfo> queue = Lists.newArrayList();

    @Override
    public void add(@NotNull RawIdentifierInfo identifier) {
        this.queue.add(identifier);
        if (this.queue.size() >= 5000) {
            persistQueuedItems();
        }
    }

    @Override
    public void close() {
        if (!this.queue.isEmpty()) {
            persistQueuedItems();
        }
    }

    private void persistQueuedItems() {
        //
        log.info("flushing {} records", this.queue.size());

        //
        List<IdentifierInfoEntity> identifierInfoEntities = this.sharedData
            .getIdentifierInfoEntityRepository()
            .save(Lists.transform(this.queue, this::toEntity));

        //
        int index = 0;
        List<IdentifierMappingEntity> identifierMappingEntities = Lists.newArrayList();

        for (RawIdentifierInfo identifier : this.queue) {
            //
            Collection<RawIdentifierMapping> mappings = identifier.getMappings();
            if (!mappings.isEmpty()) {
                IdentifierInfoEntity parentEntity = identifierInfoEntities.get(index);
                mappings.stream().map(m -> toEntity(m, parentEntity)).forEach(identifierMappingEntities::add);
            }

            //
            index++;
        }

        this.sharedData.getIdentifierMappingEntityRepository().save(identifierMappingEntities);

        //
        this.queue.clear();
    }

    private IdentifierInfoEntity toEntity(@NotNull RawIdentifierInfo identifier) {
        IdentifierInfoEntity iie = new IdentifierInfoEntity();
        iie.setDatasetId(this.dataset.getId());
        iie.setPriority(1);
        iie.setIdentifier(identifier.getId());
        iie.setName(identifier.getName().orElse(null));
        iie.setDescription(identifier.getDescription().orElse(null));
        return iie;
    }

    private IdentifierMappingEntity toEntity(
        @NotNull RawIdentifierMapping identifierMapping, @NotNull IdentifierInfoEntity parentEntity) {

        IdentifierMappingEntity ime = new IdentifierMappingEntity();
        ime.setIdentifierInfo(parentEntity);
        ime.setType(identifierMapping.getType());
        ime.setHint(identifierMapping.getHint().orElse(null));
        ime.setTargetIdentifier(identifierMapping.getIdentifier());
        return ime;
    }

}
