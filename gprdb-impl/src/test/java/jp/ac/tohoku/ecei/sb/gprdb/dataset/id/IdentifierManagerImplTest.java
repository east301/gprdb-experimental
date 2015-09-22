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

import com.google.common.collect.ImmutableList;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.DatasetObjectNotFoundException;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifierManagerImplTest {

    ///
    /// setup
    ///

    @Mocked
    private IdentifierInternalSharedData sharedData;

    private IdentifierManagerImpl manager;

    @BeforeMethod
    public void setUp() {
        manager = new IdentifierManagerImpl(this.sharedData);
    }

    ///
    /// getDatasets
    ///

    @Test
    public void getDatasets_returns_correct_result(
        @Mocked IdentifierConfig.DatasetConfig config1,
        @Mocked IdentifierConfig.DatasetConfig config2) {

        new NonStrictExpectations() {{
            sharedData.getRootConfig().getDatasets();
            result = ImmutableList.of(config1, config2);

            config1.getId(); result = "foo";
            config2.getId(); result = "bar";
        }};

        List<IdentifierDataset> datasets = manager.getDatasets();
        assertThat(datasets).hasSize(2);
        assertThat(datasets.get(0).getId()).isEqualTo("foo");
        assertThat(datasets.get(1).getId()).isEqualTo("bar");

        new Verifications() {{
            config1.getId(); times = 1;
            config2.getId(); times = 1;
        }};
    }

    @Test
    public void getDatasets_returns_correct_result__empty() {
        new NonStrictExpectations() {{
            sharedData.getRootConfig().getDatasets();
            result = Collections.emptyList();
        }};

        List<IdentifierDataset> datasets = manager.getDatasets();
        assertThat(datasets).isNotNull().isEmpty();
    }

    ///
    /// getDataset
    ///

    @Test
    public void getDataset_returns_correct_result(
        @Mocked IdentifierConfig.DatasetConfig config1,
        @Mocked IdentifierConfig.DatasetConfig config2) {

        new NonStrictExpectations() {{
            sharedData.getRootConfig().getDatasets();
            result = ImmutableList.of(config1, config2);

            config1.getId(); result = "foo";
            config2.getId(); result = "bar";
        }};

        assertThat(manager.getDataset("foo").getId()).isEqualTo("foo");
        assertThat(manager.getDataset("bar").getId()).isEqualTo("bar");
    }

    @Test(
        expectedExceptions = DatasetObjectNotFoundException.class,
        expectedExceptionsMessageRegExp = "identifier dataset not found: bar"
    )
    public void getDataset_throws_an_exception_if_not_found(
        @Mocked IdentifierConfig.DatasetConfig config1) {

        new NonStrictExpectations() {{
            sharedData.getRootConfig().getDatasets();
            result = ImmutableList.of(config1);

            config1.getId(); result = "foo";
        }};

        manager.getDataset("bar");
    }

    ///
    /// wrap
    ///

    @Test
    public void wrap_returns_instance_of_which_getId_returns_the_id() {
        Identifier identifier = manager.wrap("foo:bar");
        assertThat(identifier).isNotNull();
        assertThat(identifier.getId()).isEqualTo("foo:bar");
    }

}
