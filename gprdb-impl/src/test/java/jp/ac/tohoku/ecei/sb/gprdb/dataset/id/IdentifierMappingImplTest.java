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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifierMappingImplTest {

    ///
    /// setup
    ///

    @Mocked
    private IdentifierInternalSharedData sharedData;

    @Mocked
    private IdentifierMappingEntity entity;

    private IdentifierMappingImpl impl;

    @BeforeMethod
    public void setUp() {
        impl = new IdentifierMappingImpl(sharedData, entity);
    }

    ///
    /// getType
    ///

    @Test
    public void test_getType__with_hint(
        @Mocked IdentifierMappingTypeFactory identifierMappingTypeFactory,
        @Mocked IdentifierMappingType identifierMappingType) {

        new NonStrictExpectations() {{
            entity.getType(); result = "foo";
            entity.getHint(); result = "bar";

            sharedData.getIdentifierMappingTypeFactory();
            result = identifierMappingTypeFactory;

            identifierMappingTypeFactory.newCustomMappingType("foo", "bar");
            result = identifierMappingType;
        }};

        assertThat(impl.getType()).isSameAs(identifierMappingType);

        new Verifications() {{
            identifierMappingTypeFactory.newCustomMappingType("foo", "bar"); times = 1;
        }};
    }

    @Test
    public void test_getType__without_hint(
        @Mocked IdentifierMappingTypeFactory identifierMappingTypeFactory,
        @Mocked IdentifierMappingType identifierMappingType) {

        new NonStrictExpectations() {{
            entity.getType(); result = "foo";
            entity.getHint(); result = null;

            sharedData.getIdentifierMappingTypeFactory();
            result = identifierMappingTypeFactory;

            identifierMappingTypeFactory.newCustomMappingType("foo");
            result = identifierMappingType;
        }};

        assertThat(impl.getType()).isSameAs(identifierMappingType);

        new Verifications() {{
            identifierMappingTypeFactory.newCustomMappingType("foo"); times = 1;
        }};
    }

    ///
    /// getTargetIdentifier
    ///

    @Test
    public void test_getTargetIdentifier() {
        new NonStrictExpectations() {{
            entity.getTargetIdentifier(); result = "id";
        }};

        assertThat(impl.getTargetIdentifier().getId()).isEqualTo("id");

        new Verifications() {{
            entity.getTargetIdentifier(); times = 1;
        }};
    }

    ///
    /// equals
    ///

    @Test
    public void test_equals__true(@Mocked IdentifierMappingEntity otherEntity) {
        new NonStrictExpectations() {{
            entity.equals(otherEntity); result = true;
            otherEntity.equals(entity); result = true;
        }};

        IdentifierMappingImpl otherImpl = new IdentifierMappingImpl(sharedData, otherEntity);
        assertThat(impl.equals(otherImpl)).isTrue();
        assertThat(otherImpl.equals(impl)).isTrue();

        new Verifications() {{
            entity.equals(otherEntity); times = 1;
            otherEntity.equals(entity); times = 1;
        }};
    }

    @Test
    public void test_equals__false(@Mocked IdentifierMappingEntity otherEntity) {
        new NonStrictExpectations() {{
            entity.equals(otherEntity); result = false;
            otherEntity.equals(entity); result = false;
        }};

        IdentifierMappingImpl otherImpl = new IdentifierMappingImpl(sharedData, otherEntity);
        assertThat(impl.equals(otherImpl)).isFalse();
        assertThat(otherImpl.equals(impl)).isFalse();

        new Verifications() {{
            entity.equals(otherEntity); times = 1;
            otherEntity.equals(entity); times = 1;
        }};
    }

}
