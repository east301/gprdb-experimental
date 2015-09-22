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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Reads gene-pair relation strength values from underlying data source.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationReader extends AutoCloseable {

    /**
     * Gets genes.
     *
     * @return  genes
     */
    @NotNull
    List<Identifier> getGenes();

    /**
     * Checks whether the underlying dataset contains a gene or not.
     *
     * @param gene  gene to be tested
     * @return  {@code true} if the underlying dataset contans the gene, otherwise {@code false}
     */
    boolean containsGene(@NotNull Identifier gene);

    /**
     * Reads gene-pair relation strength values.
     *
     * @param type      relation type
     * @param guideGene guide gene
     * @return  relation strength values
     */
    @NotNull
    Map<Identifier, Double> readMap(@NotNull GenePairRelationType type, @NotNull Identifier guideGene);

    /**
     * Disposes the reader.
     */
    @Override
    void close();

}
