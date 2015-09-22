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

package jp.ac.tohoku.ecei.sb.gprdb.io;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * An implementation of {@link KeyValueStoreManager} which uses MapDB as backend.
 *
 * @author Shu Tadaka
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class KeyValueStoreManagerImpl implements KeyValueStoreManager {

    private final StorageManager storageManager;

    @Override
    public KeyValueStore<String, String> getMap(@NotEmpty String id) {
        return getMapInternal(id);
    }

    @Override
    public <T, U> KeyValueStore<T, U> getMap(
        @NotEmpty String id, @NotNull Class<T> keyType, @NotNull Class<U> valueType) {

        return getMapInternal(id);
    }

    private <T, U> KeyValueStore<T, U> getMapInternal(@NotEmpty String id) {
        DBMaker.Maker maker = DBMaker
            .fileDB(this.storageManager.getFile(id + ".mapdb"))
            .mmapFileEnableIfSupported()
            .fileChannelEnable()
            .closeOnJvmShutdown();

        if (this.storageManager.isReadOnlyModePreferred()) {
            maker = maker.readOnly();
        }

        return new KeyValueStoreImpl<>(maker.make());
    }

}
