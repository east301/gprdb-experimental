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

import javax.validation.constraints.NotNull;

/**
 * Factory of {@link IdentifierMappingType}s.
 *
 * @author Shu Tadaka
 */
public interface IdentifierMappingTypeFactory {

    /**
     * Creates a mapping type which is used when representing a mapping between two gene identifiers
     * of a same species.
     *
     * @return  intra-species mapping type
     */
    @NotNull
    IdentifierMappingType newIntraSpeciesIdentifierMappingType();

    /**
     * Creates a mapping type which is used when representing a mapping between two gene identifiers
     * of two different species.
     *
     * @param destinationSpecies    destination species
     * @return  inter-species mapping type
     */
    @NotNull
    IdentifierMappingType newInterSpeciesIdentifierMappingType(@NotNull Identifier destinationSpecies);

    /**
     * Creates a mapping type which is used when representing a mapping between gene identifier
     * and functional annotation for the gene.
     *
     * @return  functional annotation mapping type
     */
    @NotNull
    IdentifierMappingType newFunctionalAnnotationIdentifierMappingType();

    /**
     * Creates a new custom mapping type.
     *
     * @param type  type
     * @return  custom type
     */
    @NotNull
    IdentifierMappingType newCustomMappingType(@NotEmpty String type);

    /**
     * Creates a new custom mapping type.
     *
     * @param type  type
     * @param hint  mapping hint
     * @return  custom type
     */
    @NotNull
    IdentifierMappingType newCustomMappingType(@NotEmpty String type, @NotEmpty String hint);

}
