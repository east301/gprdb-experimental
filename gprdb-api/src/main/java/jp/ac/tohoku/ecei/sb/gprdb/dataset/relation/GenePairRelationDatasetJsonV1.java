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

package jp.ac.tohoku.ecei.sb.gprdb.dataset.relation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Shu Tadaka
 */
public interface GenePairRelationDatasetJsonV1 {

    /**
     * Gets ID.
     *
     * @return  Id
     */
    @NotEmpty
    @JsonProperty(value = "id")
    String getId();

    /**
     * Gets name.
     *
     * @return  name.
     */
    @NotEmpty
    @JsonProperty(value = "name")
    String getName();

    /**
     * Gets short name.
     *
     * @return  short name
     */
    @NotEmpty
    @JsonProperty(value = "shortName")
    String getShortName();

    /**
     * Gets version.
     *
     * @return  version
     */
    @NotEmpty
    @JsonProperty(value = "version")
    String getVersion();

    /**
     * Gets relation type IDs.
     *
     * @return  relation type IDs
     */
    @NotNull
    @JsonProperty(value = "relationTypes")
    List<String> getRelationTypes();

    /**
     * Gets species.
     *
     * @return  species
     */
    @NotEmpty
    @JsonProperty(value = "species")
    String getSpecies();

    /**
     * Gets number of genes.
     *
     * @return  number of genes
     */
    @Min(0)
    @JsonProperty(value = "numGenes")
    int getNumGenes();

}
