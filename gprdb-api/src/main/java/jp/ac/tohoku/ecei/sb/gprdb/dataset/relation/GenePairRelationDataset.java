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
import jp.ac.tohoku.ecei.sb.gprdb.bean.Identifiable;
import jp.ac.tohoku.ecei.sb.gprdb.bean.Versioned;
import jp.ac.tohoku.ecei.sb.gprdb.io.Loadable;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents a dataset that contains gene-pair relation information.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationDataset extends Identifiable, Versioned, Loadable {

    /**
     * Gets relation types.
     *
     * @return  relation types
     */
    @NotNull
    List<GenePairRelationType> getRelationTypes();

    /**
     * Gets species.
     *
     * @return  species
     */
    @NotEmpty
    Identifier getSpecies();

    /**
     * Indicates whether the dataset is shown as primary dataset (visible) or not (hidden).
     *
     * @return  {@code true} if hidden, otherwise {@code false} (visible)
     */
    boolean isHidden();

    /**
     * Gets genes in the dataset.
     *
     * @return  genes
     */
    @NotNull
    List<Identifier> getGenes();

    /**
     * Gets number of genes in the dataset.
     * Equivalent to {@code GenePairRelationDataset.getGenes().size()}.
     *
     * @return  number of genes
     */
    @Min(0)
    int getNumGenes();

    /**
     * Creates a new reader to read relation strength values.
     *
     * @return  reader
     */
    @NotNull
    GenePairRelationReader newReader();

}
