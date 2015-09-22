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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifierMappingTypeFactoryImplTest {

    ///
    /// setup
    ///

    private IdentifierMappingTypeFactoryImpl factory;

    @BeforeMethod
    public void setUp() {
        factory = new IdentifierMappingTypeFactoryImpl();
    }

    ///
    /// newIntraSpeciesIdentifierMappingType
    ///
    
    @Test
    public void test_newIntraSpeciesIdentifierMappingType() {
        IdentifierMappingType type = factory.newIntraSpeciesIdentifierMappingType();
        assertThat(type.getType()).isEqualTo(IdentifierMappingTypes.INTRA_SPECIES);
        assertThat(type.getHint()).isEmpty();
    }

    ///
    /// newInterSpeciesIdentifierMappingType
    ///

    @Test
    public void test_newInterSpeciesIdentifierMappingType(@Mocked Identifier identifier) {
        new NonStrictExpectations() {{
            identifier.getId(); result = "taxonomy:12345";
        }};

        IdentifierMappingType type = factory.newInterSpeciesIdentifierMappingType(identifier);
        assertThat(type.getType()).isEqualTo(IdentifierMappingTypes.INTER_SPECIES);
        assertThat(type.getHint()).isPresent().contains("taxonomy:12345");
    }

    ///
    /// newFunctionalAnnotationIdentifierMappingType
    ///

    @Test
    public void test_newFunctionalAnnotationIdentifierMappingType() {
        IdentifierMappingType type = factory.newFunctionalAnnotationIdentifierMappingType();
        assertThat(type.getType()).isEqualTo(IdentifierMappingTypes.FUNCTION);
        assertThat(type.getHint()).isEmpty();
    }

    ///
    /// newCustomMappingType(type)
    ///

    @Test
    public void test_newCustomMappingType() {
        IdentifierMappingType type = factory.newCustomMappingType("foo");
        assertThat(type.getType()).isEqualTo("foo");
        assertThat(type.getHint()).isEmpty();
    }

    ///
    /// newCustomMappingType(type, hint)
    ///

    @Test
    public void test_newCustomMappingType__with_hint() {
        IdentifierMappingType type = factory.newCustomMappingType("foo", "bar");
        assertThat(type.getType()).isEqualTo("foo");
        assertThat(type.getHint()).isPresent().contains("bar");
    }

}
