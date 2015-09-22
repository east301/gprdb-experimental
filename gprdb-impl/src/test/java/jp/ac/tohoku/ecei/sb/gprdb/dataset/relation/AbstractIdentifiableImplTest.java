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

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class AbstractIdentifiableImplTest {

    ///
    /// implementation for test
    ///

    public static class Config extends GenePairRelationConfig.IdentifiableConfig {
        // empty
    }

    public static class Impl extends AbstractIdentifiableImpl<Config> {
        @SuppressWarnings("unchecked")
        public Impl(Config config) {
            super(config);
        }
    }

    ///
    /// setup
    ///

    @Mocked
    private Config config1;

    private Impl impl1;

    @BeforeMethod
    public void setUp() {
        this.impl1 = new Impl(this.config1);
    }

    ///
    /// getId
    ///

    @Test
    public void getId_returns_correct_result() {
        new NonStrictExpectations() {{
            config1.getId(); result = "foo";
        }};

        assertThat(impl1.getId()).isEqualTo("foo");

        new Verifications() {{
            config1.getId(); times = 1;
        }};
    }

    ///
    /// getName
    ///

    @Test
    public void getName_returns_correct_result() {
        new NonStrictExpectations() {{
            config1.getName(); result = "bar";
        }};

        assertThat(impl1.getName()).isEqualTo("bar");

        new Verifications() {{
            config1.getName(); times = 1;
        }};
    }

    ///
    /// getShortName
    ///

    @Test
    public void getShortName_returns_correct_result() {
        new NonStrictExpectations() {{
            config1.getShortName(); result = "baz";
        }};

        assertThat(impl1.getShortName()).isEqualTo("baz");

        new Verifications() {{
            config1.getShortName(); times = 1;
        }};
    }

    ///
    /// equals & hashCode
    ///

    @Test
    public void equals_and_hashCode_returns_correct_result(@Mocked Config config2, @Mocked Config config3) {
        new NonStrictExpectations() {{
            config1.getId(); result = "foo";
            config2.getId(); result = "foo";
            config3.getId(); result = "bar";
        }};

        Impl impl2 = new Impl(config2);
        Impl impl3 = new Impl(config3);

        assertThat(impl1).isNotEqualTo(null).isEqualTo(impl1).isEqualTo(impl2).isNotEqualTo(impl3);
        assertThat(impl2).isNotEqualTo(null).isEqualTo(impl1).isEqualTo(impl2).isNotEqualTo(impl3);
        assertThat(impl3).isNotEqualTo(null).isNotEqualTo(impl1).isNotEqualTo(impl2).isEqualTo(impl3);

        assertThat(impl1.hashCode()).isEqualTo(impl2.hashCode());
    }

}
