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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Manages {@link GenePairRelationDataset}s and {@link GenePairRelationType}s.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationManager {

    /**
     * Gets relation types.
     *
     * @return  relation types
     */
    @NotNull
    List<GenePairRelationType> getTypes();

    /**
     * Gets relation type with a specified ID.
     *
     * @param id    ID
     * @return  relation type
     */
    @NotNull
    GenePairRelationType getType(@NotEmpty String id);

    /**
     * Gets datasets. Equivalent to {@code #getDatasets(false)}.
     *
     * @return  datasets
     */
    @NotNull
    List<GenePairRelationDataset> getDatasets();

    /**
     * Gets datasets.
     *
     * @param includeHidden if {@code true}, result includes hidden datasets.
     * @return  datasets
     */
    @NotNull
    List<GenePairRelationDataset> getDatasets(boolean includeHidden);

    /**
     * Gets a dataset with a specified ID. If no dataset has the specified ID,
     * throws {@link jp.ac.tohoku.ecei.sb.gprdb.dataset.DatasetObjectNotFoundException}.
     *
     * @param id    ID
     * @return  dataset
     */
    @NotNull
    GenePairRelationDataset getDataset(@NotEmpty String id);

    /**
     * Gets {@link GenePairRelationAccessorFactory}.
     *
     * @return  accessor factory
     */
    @NotNull
    GenePairRelationAccessorFactory getAccessorFactory();

    /**
     * Creates a gene-pair relation table.
     *
     * @param guideDataset  guide dataset
     * @param guideGene     guide gene
     * @param relationType  relation type
     * @param start         start index
     * @param count         count
     * @return  relation table
     */
    @NotNull
    GenePairRelationTable createTable(
        @NotNull GenePairRelationDataset guideDataset,
        @NotNull Identifier guideGene,
        @NotNull GenePairRelationType relationType,
        @Min(0) int start,
        @Min(1) int count);
    
}
