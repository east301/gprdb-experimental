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

import javax.validation.constraints.Min;

/**
 * Abstraction of key-value store.
 *
 * @param <T>   type of key
 * @param <U>   type of value
 *
 * @author Shu Tadaka
 */
public interface KeyValueStore<T, U> extends AutoCloseable {

    /**
     * Gets a value associated to a specified key. If a specified entry does not exist, returns {@code null}.
     *
     * @param key           key
     * @return  value associated to a key
     */
    U get(T key);

    /**
     * Gets a value associated to a specified key. If a specified entry does not exist, returns {@code defaultValue}.
     *
     * @param key           key
     * @param defaultValue  default value
     * @return  value associated to a key
     */
    U getOrDefault(T key, U defaultValue);

    /**
     * Puts an entry.
     *
     * @param key   key
     * @param value value
     */
    void put(T key, U value);

    /**
     * Checks existence of a specified entry.
     *
     * @param key   key
     * @return  {@code true} if a specified entry exists, otherwise {@code false}
     */
    boolean containsKey(T key);

    /**
     * Gets size.
     *
     * @return  size
     */
    @Min(0)
    int size();

    /**
     * Clears all key-value entries.
     */
    void clear();

    /**
     * Commits changes.
     */
    void commit();

    /**
     * Optimize datastore.
     */
    void optimize();

    /**
     * Closes the KVS.
     */
    @Override
    void close();

}
