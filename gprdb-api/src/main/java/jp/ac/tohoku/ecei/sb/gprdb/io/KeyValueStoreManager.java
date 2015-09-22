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

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Factory of {@link KeyValueStore}.
 *
 * @author Shu Tadaka
 */
public interface KeyValueStoreManager {

    /**
     * Gets a {@link KeyValueStore} with a specified ID.
     *
     * @param id        ID
     * @return  map
     */
    @NotNull
    KeyValueStore<String, String> getMap(@NotEmpty String id);

    /**
     * Gets a {@link KeyValueStore} with a specified ID.
     *
     * @param id        ID
     * @param keyType   key type
     * @param valueType value type
     * @return  map
     */
    @NotNull
    <T, U> KeyValueStore<T, U> getMap(
        @NotEmpty String id, @NotNull Class<T> keyType, @NotNull Class<U> valueType);

}
