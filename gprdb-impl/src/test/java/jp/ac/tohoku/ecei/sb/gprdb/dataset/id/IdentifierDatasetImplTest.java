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

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifierDatasetImplTest {

    ///
    /// setup
    ///

    @Mocked
    private IdentifierConfig.DatasetConfig config;

    private IdentifierDatasetImpl impl;

    @BeforeMethod
    public void setUp() {
        this.impl = new IdentifierDatasetImpl(this.config);
    }

    ///
    /// getId
    ///

    @Test
    public void test_getId() {
        new NonStrictExpectations() {{
            config.getId(); result = "foo";
        }};

        assertThat(impl.getId()).isEqualTo("foo");

        new Verifications() {{
            config.getId(); times = 1;
        }};
    }

    ///
    /// getName
    ///

    @Test
    public void test_getName() {
        new NonStrictExpectations() {{
            config.getName(); result = "foo";
        }};

        assertThat(impl.getName()).isEqualTo("foo");

        new Verifications() {{
            config.getName(); times = 1;
        }};
    }

    ///
    /// getShortName
    ///

    @Test
    public void test_getShortName() {
        new NonStrictExpectations() {{
            config.getShortName(); result = "foo";
        }};

        assertThat(impl.getShortName()).isEqualTo("foo");

        new Verifications() {{
            config.getShortName(); times = 1;
        }};
    }

    ///
    /// getVersion
    ///

    @Test
    public void test_getVersion() {
        new NonStrictExpectations() {{
            config.getVersion(); result = "foo";
        }};

        assertThat(impl.getVersion()).isEqualTo("foo");

        new Verifications() {{
            config.getVersion(); times = 1;
        }};
    }

    ///
    /// getLoaderFactoryClass
    ///

    @Test
    public void test_getLoaderFactoryClass() {
        new NonStrictExpectations() {{
            config.getLoaderFactoryClass(); result = "foo";
        }};

        assertThat(impl.getLoaderFactoryClass()).isEqualTo("foo");

        new Verifications() {{
            config.getLoaderFactoryClass(); times = 1;
        }};
    }

    ///
    /// getLoaderConfig
    ///

    @Test
    public void test_getLoaderConfig(@Mocked Map<String, Object> loaderConfig) {
        new NonStrictExpectations() {{
            config.getLoaderConfig(); result = loaderConfig;
        }};

        assertThat(impl.getLoaderConfig()).isSameAs(loaderConfig);

        new Verifications() {{
            config.getLoaderConfig(); times = 1;
        }};
    }

}
