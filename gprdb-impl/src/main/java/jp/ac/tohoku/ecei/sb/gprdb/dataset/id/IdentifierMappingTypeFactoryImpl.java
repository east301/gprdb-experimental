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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Component
class IdentifierMappingTypeFactoryImpl implements IdentifierMappingTypeFactory {

    private static final IdentifierMappingType INTRA_SPECIES
        = new IdentifierMappingTypeImpl(IdentifierMappingTypes.INTRA_SPECIES, Optional.empty());

    private static final IdentifierMappingType FUNCTION
        = new IdentifierMappingTypeImpl(IdentifierMappingTypes.FUNCTION, Optional.empty());

    @Override
    public IdentifierMappingType newIntraSpeciesIdentifierMappingType() {
        return INTRA_SPECIES;
    }

    @Override
    public IdentifierMappingType newInterSpeciesIdentifierMappingType(
        @NotNull Identifier destinationSpecies) {

        return new IdentifierMappingTypeImpl(
            IdentifierMappingTypes.INTER_SPECIES, Optional.of(destinationSpecies.getId()));
    }

    @Override
    public IdentifierMappingType newFunctionalAnnotationIdentifierMappingType() {
        return FUNCTION;
    }

    @Override
    public IdentifierMappingType newCustomMappingType(@NotEmpty String type) {
        return new IdentifierMappingTypeImpl(type, Optional.empty());
    }

    @Override
    public IdentifierMappingType newCustomMappingType(@NotEmpty String type, @NotEmpty String hint) {
        return new IdentifierMappingTypeImpl(type, Optional.of(hint));
    }

}
