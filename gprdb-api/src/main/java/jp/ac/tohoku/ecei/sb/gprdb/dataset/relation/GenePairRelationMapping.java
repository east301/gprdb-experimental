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
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;

import javax.validation.constraints.NotNull;

/**
 * Represents a mapping result of gene-pair relation.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationMapping {

    /**
     * Gets gene.
     *
     * @return  gene
     */
    @NotNull
    @JsonProperty
    Identifier getGene();

    /**
     * Gets relation strength.
     *
     * @return  relation strength
     */
    @NotNull
    @JsonProperty
    double getStrength();

}
