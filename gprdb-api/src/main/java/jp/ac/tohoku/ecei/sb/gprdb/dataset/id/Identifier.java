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
import org.openrdf.model.IRI;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Represents an identifier of a gene or a gene annotation.
 *
 * "Identifier" in gprdb consists of two parts: prefix and body.
 * For example, an prefix and body of an identifier "foo:bar" are "foo" and "bar" respectively.
 *
 * All implementation of {@link Identifier} must implement {@code Identifier#equals(Object)} and
 * {@code Identifier#hashCode()}. {@code identifier1.equals(identifier2)} must return same value as
 * {@code identifier1.getId().equals(identifier2.getId())}, and {@code identifier1.hashCode()} must
 * return same value as {@code identifier1.getId().hashCode()}.
 *
 * @author Shu Tadaka
 */
public interface Identifier {

    /**
     * Gets ID.
     *
     * @return  ID
     */
    @NotEmpty
    String getId();

    /**
     * Gets prefix.
     *
     * @return  prefix
     */
    @NotEmpty
    String getPrefix();

    /**
     * Gets body.
     *
     * @return  body
     */
    @NotEmpty
    String getBody();

    /**
     * Gets name.
     *
     * @return  name
     */
    @NotNull
    Optional<String> getName();

    /**
     * Gets description.
     *
     * @return  description
     */
    @NotNull
    Optional<String> getDescription();

    /**
     * Gets URI for RDF outputs.
     *
     * @return  URI
     */
    @NotNull
    Optional<IRI> getUri();

    /**
     * Gets link.
     *
     * @return  link
     */
    @NotNull
    Optional<IdentifierLink> getLink();

    /**
     * Gets mappings.
     *
     * @param type  mapping type
     * @return  mappings
     */
    @NotNull
    List<Identifier> getMappingResults(@NotNull IdentifierMappingType type);

    /**
     * Gets information.
     *
     * @return  information
     */
    @NotNull
    List<IdentifierInfo> getInfo();

    /**
     * Gets links.
     *
     * @return  links
     */
    @NotNull
    List<IdentifierLink> getLinks();

}
