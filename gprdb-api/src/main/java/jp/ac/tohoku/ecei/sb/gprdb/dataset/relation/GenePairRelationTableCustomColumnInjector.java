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

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Customizes gene-pair relation table by injecting custom column.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationTableCustomColumnInjector extends AutoCloseable {

    /**
     * Gets name.
     *
     * @return  name
     */
    @NotEmpty
    String getName();

    /**
     * Gets position of injected columns.
     *
     * @return  position of injected column
     */
    @NotNull
    GenePairRelationTableCustomColumnPosition getPosition();

    /**
     * Performs preparation.
     *
     * @param table relation table
     */
    void prepare(@NotNull GenePairRelationTable table);

    /**
     * Gets HTML to be placed into a cell in a relation table.
     *
     * @param interactingGene   interacting gene
     * @return  html text
     */
    @NotNull
    String getCellAsHtml(@NotNull Identifier interactingGene);

    /**
     * Cleans up internal resources.
     */
    @Override
    void close();

}
