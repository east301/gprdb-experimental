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

import org.openrdf.model.IRI;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

/**
 * Resolves an identifier's URI used in RDF-style output.
 *
 * @author Shu Tadaka
 */
public interface IdentifierUriResolver {

    /**
     * Gets prefixes of supported identifiers.
     *
     * @return  prefixes of supported identifiers
     */
    @NotNull
    Collection<String> getSupportedIdentifierPrefixes();

    /**
     * Resolves URI for an identifier.
     *
     * @param id    identifier
     * @return  URI for an identifier
     */
    @NotNull
    Optional<IRI> resolve(@NotNull Identifier id);

}
