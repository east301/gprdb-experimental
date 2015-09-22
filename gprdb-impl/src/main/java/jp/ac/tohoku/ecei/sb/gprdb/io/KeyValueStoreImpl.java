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

import org.mapdb.DB;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * An implementation of {@link KeyValueStore} which uses MapDB as backend.
 *
 * @author Shu Tadaka
 */
class KeyValueStoreImpl<T, U> implements KeyValueStore<T, U> {

    private final DB database;
    private final Map<T, U> map;

    public KeyValueStoreImpl(@NotNull DB database) {
        this.database = database;
        this.map = database.treeMap("map");
    }

    @Override
    public U get(T key) {
        return this.map.get(key);
    }

    @Override
    public U getOrDefault(T key, U defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }

    @Override
    public void put(T key, U value) {
        this.map.put(key, value);
    }

    @Override
    public boolean containsKey(T key) {
        return this.map.containsKey(key);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public void commit() {
        this.database.commit();
    }

    @Override
    public void optimize() {
        this.database.compact();
    }

    @Override
    public void close() {
        this.database.close();
    }

}
