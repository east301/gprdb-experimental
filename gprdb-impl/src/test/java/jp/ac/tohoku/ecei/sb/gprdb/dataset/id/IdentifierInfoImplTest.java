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
import com.google.common.collect.ImmutableMap;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifierInfoImplTest {

    ///
    /// setup
    ///

    @Mocked
    private IdentifierInternalSharedData sharedData;

    @Mocked
    private IdentifierInfoEntity entity;

    private IdentifierInfoImpl impl;

    @BeforeMethod
    public void setUp() throws Exception {
        impl = new IdentifierInfoImpl(sharedData, entity);
    }

    ///
    /// getDataset
    ///

    @Test
    public void test_getDataset(@Mocked IdentifierDataset dataset) {
        new NonStrictExpectations() {{
            sharedData.getIdentifierDatasetMap(); result = ImmutableMap.of("foo", dataset);
            entity.getDatasetId(); result = "foo";
        }};

        assertThat(impl.getDataset()).isSameAs(dataset);
    }

    ///
    /// getName
    ///

    @Test
    public void test_getName() {
        new NonStrictExpectations() {{
            entity.getName(); result = "foo";
        }};

        assertThat(impl.getName()).isPresent().contains("foo");

        new Verifications() {{
            entity.getName(); times = 1;
        }};
    }

    @Test
    public void test_getName__empty() {
        new NonStrictExpectations() {{
            entity.getName(); result = null;
        }};

        assertThat(impl.getName()).isEmpty();

        new Verifications() {{
            entity.getName(); times = 1;
        }};
    }

    ///
    /// getDescription
    ///

    @Test
    public void test_getDescription() {
        new NonStrictExpectations() {{
            entity.getDescription(); result = "foo";
        }};

        assertThat(impl.getDescription()).isPresent().contains("foo");

        new Verifications() {{
            entity.getDescription(); times = 1;
        }};
    }

    @Test
    public void test_getDescription__empty() {
        new NonStrictExpectations() {{
            entity.getDescription(); result = null;
        }};

        assertThat(impl.getDescription()).isEmpty();

        new Verifications() {{
            entity.getDescription(); times = 1;
        }};
    }

    ///
    /// getMappings
    ///

    @Test
    public void test_getMappings(
        @Mocked IdentifierMappingEntity mappingEntity1,
        @Mocked IdentifierMappingEntity mappingEntity2) {

        new NonStrictExpectations() {{
            entity.getMappings(); result = ImmutableList.of(mappingEntity1, mappingEntity2);
            mappingEntity1.getTargetIdentifier(); result = "hoge";
            mappingEntity2.getTargetIdentifier(); result = "piyo";
        }};


        List<IdentifierMapping> mappings = ImmutableList.copyOf(impl.getMappings());
        assertThat(mappings).hasSize(2);
        assertThat(mappings.get(0).getTargetIdentifier().getId()).isEqualTo("hoge");
        assertThat(mappings.get(1).getTargetIdentifier().getId()).isEqualTo("piyo");
    }

    ///
    /// equals
    ///

    @Test
    public void test_equals__true(
        @Mocked IdentifierInfoEntity entity1, @Mocked IdentifierInfoEntity entity2) {

        new NonStrictExpectations() {{
            entity1.equals(entity2); result = true;
            entity2.equals(entity1); result = true;
        }};

        IdentifierInfoImpl impl1 = new IdentifierInfoImpl(sharedData, entity1);
        IdentifierInfoImpl impl2 = new IdentifierInfoImpl(sharedData, entity2);

        assertThat(impl1.equals(impl2)).isTrue();
        assertThat(impl2.equals(impl1)).isTrue();

        new Verifications() {{
            entity1.equals(entity2); times = 1;
            entity2.equals(entity1); times = 1;
        }};
    }

    @Test
    public void test_equals__false(
        @Mocked IdentifierInfoEntity entity1, @Mocked IdentifierInfoEntity entity2) {

        new NonStrictExpectations() {{
            entity1.equals(entity2); result = false;
            entity2.equals(entity1); result = false;
        }};

        IdentifierInfoImpl impl1 = new IdentifierInfoImpl(sharedData, entity1);
        IdentifierInfoImpl impl2 = new IdentifierInfoImpl(sharedData, entity2);

        assertThat(impl1.equals(impl2)).isFalse();
        assertThat(impl2.equals(impl1)).isFalse();

        new Verifications() {{
            entity1.equals(entity2); times = 1;
            entity2.equals(entity1); times = 1;
        }};
    }

}
