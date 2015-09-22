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

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author Shu Tadaka
 */
public interface IdentifierLinkResolver {

    /**
     * Gets supported identifier prefixes.
     *
     * @return  supported prefixes
     */
    @NotNull
    Collection<String> getSupportedIdentifierPrefixes();

    /**
     * Generates link for an identifier.
     *
     * @param identifier    identifier
     * @return  link URLs
     */
    @NotNull
    List<IdentifierLink> generate(@NotNull Identifier identifier);

}
