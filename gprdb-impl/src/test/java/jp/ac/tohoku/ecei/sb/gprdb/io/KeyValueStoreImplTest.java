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

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class KeyValueStoreImplTest {

    ///
    /// setup
    ///

    @Mocked
    private DB database;

    @Mocked
    private BTreeMap<String, String> map;

    private KeyValueStoreImpl<String, String> impl;

    @BeforeMethod
    public void setUp() {
        new NonStrictExpectations() {{
            database.treeMap("map"); result = map;
        }};
        impl = new KeyValueStoreImpl<>(database);
    }

    ///
    /// constructor
    ///

    @Test
    public void test_constructor() {
        new Verifications() {{
            database.treeMap("map"); times = 1;
        }};
    }

    ///
    /// get
    ///

    @Test
    public void test_get() {
        new NonStrictExpectations() {{
            map.get("foo"); result = "bar";
        }};

        assertThat(impl.get("foo")).isEqualTo("bar");

        new Verifications() {{
            map.get("foo"); times = 1;
        }};
    }

    @Test
    public void test_get__not_found() {
        new NonStrictExpectations() {{
            map.get("foo"); result = "bar";
        }};

        assertThat(impl.get("bar")).isNull();

        new Verifications() {{
            map.get("bar"); times = 1;
        }};
    }

    ///
    /// getOrDefault
    ///

    @Test
    public void getOrDefault() {
        new NonStrictExpectations() {{
            map.getOrDefault("foo", "bar"); result = "baz";
        }};

        assertThat(impl.getOrDefault("foo", "bar")).isEqualTo("baz");

        new Verifications() {{
            map.getOrDefault("foo", "bar"); times = 1;
        }};
    }

    ///
    /// put
    ///

    @Test
    public void test_put() {
        impl.put("foo", "bar");

        new Verifications() {{
            map.put("foo", "bar");
        }};
    }

    ///
    /// containsKey
    ///

    @Test
    public void test_containsKey() {
        new NonStrictExpectations() {{
            map.containsKey("foo"); result = true;
            map.containsKey("bar"); result = false;
        }};

        assertThat(impl.containsKey("foo")).isTrue();
        assertThat(impl.containsKey("bar")).isFalse();

        new Verifications() {{
            map.containsKey("foo"); times = 1;
            map.containsKey("bar"); times = 1;
        }};
    }

    ///
    /// size
    ///

    @Test
    public void test_size() {
        new NonStrictExpectations() {{
            map.size(); result = 999;
        }};

        assertThat(impl.size()).isEqualTo(999);

        new Verifications() {{
            map.size(); times = 1;
        }};
    }

    ///
    /// clear
    ///

    @Test
    public void test_clear() {
        impl.clear();

        new Verifications() {{
            map.clear(); times = 1;
        }};
    }

    ///
    /// commit
    ///

    @Test
    public void test_commit() {
        impl.commit();

        new Verifications() {{
            database.commit(); times = 1;
        }};
    }

    ///
    /// optimize
    ///

    @Test
    public void test_optimize() {
        impl.optimize();

        new Verifications() {{
            database.compact(); times = 1;
        }};
    }

    ///
    /// close
    ///

    @Test
    public void test_close() {
        impl.close();

        new Verifications() {{
            database.close(); times = 1;
        }};
    }

}
