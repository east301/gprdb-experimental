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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents a gene-pair relation table.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationTable {

    /**
     * Gets primary dataset.
     *
     * @return  primary dataset
     */
    @NotNull
    @JsonProperty
    GenePairRelationDataset getGuideDataset();

    /**
     * Gets guide gene.
     *
     * @return  guide gene
     */
    @NotNull
    @JsonProperty
    Identifier getGuideGene();

    /**
     * Gets interacting genes.
     *
     * @return  interacting genes
     */
    @NotNull
    @JsonProperty
    List<Identifier> getGuideInteractingGenes();

    /**
     * Gets column.
     *
     * @return  column
     */
    @NotEmpty
    List<GenePairRelationTableColumn> getColumns();

    /**
     * Gets row size.
     *
     * @return  row size
     */
    @Min(0)
    @JsonIgnore
    int getRowSize();

    /**
     * Gets column size.
     *
     * @return  column size
     */
    @Min(1)
    @JsonIgnore
    int getColumnSize();

    /**
     * Gets custom columns.
     *
     * @return  custom columns
     */
    @NotNull
    List<GenePairRelationTableCustomColumnInjector> getCustomColumns();

}
