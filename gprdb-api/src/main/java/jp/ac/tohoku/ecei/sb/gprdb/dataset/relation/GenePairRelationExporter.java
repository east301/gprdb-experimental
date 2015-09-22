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
import org.openrdf.model.Model;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Converts gene-pair relation datasets or related stuffs to triples.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationExporter {

    ///
    /// a set of datasets
    ///

    /**
     * Creates a list of {@link GenePairRelationDatasetJsonV1}s.
     *
     * @return  model
     */
    @NotNull
    List<GenePairRelationDatasetJsonV1> getDatasetsAsJsonV1();

    /**
     * Creates a list of {@link GenePairRelationDatasetJsonV1}s.
     *
     * @return  model
     */
    @NotNull
    Model getDatasetsAsRdfV1();

    ///
    /// single dataset
    ///

    /**
     * Creates a new {@link GenePairRelationDatasetJsonV1} which represents this dataset.
     *
     * @param dataset   dataset
     * @return  dataset model
     */
    @NotNull
    GenePairRelationDatasetJsonV1 getDatasetAsJsonV1(@NotNull GenePairRelationDataset dataset);

    /**
     * Creates a new {@link Model} which represents this dataset.
     *
     * @param dataset   dataset
     * @return  dataset model
     */
    @NotNull
    Model getDatasetAsRdfV1(@NotNull GenePairRelationDataset dataset);

    /**
     * Creates a new {@link Model} which represents relation between {@code guideGene} and genes in the dataset.
     * {@link Model} created by this method only contains relations with high relation strength.
     *
     * @param dataset   dataset
     * @param guideGene     guide gene
     * @param relationType  relation type
     * @return  model
     */
    @NotNull
    Model getRelationsAsRdfV1(
        @NotNull GenePairRelationDataset dataset,
        @NotNull Identifier guideGene,
        @NotNull GenePairRelationType relationType);

    /**
     * Creates a new {@link Model} which represents relation between {@code guideGene} and genes in the dataset.
     *
     * @param dataset   dataset
     * @param guideGene     guide gene
     * @param relationType  relation type
     * @param filter        filter
     * @return  model
     */
    @NotNull
    Model getRelationsAsRdfV1(
        @NotNull GenePairRelationDataset dataset,
        @NotNull Identifier guideGene,
        @NotNull GenePairRelationType relationType,
        @NotNull Function<Map<Identifier, Double>, Collection<Identifier>> filter);

    /**
     * Creates a new {@link Model} which represents relation between a pair of two genes.
     *
     * @param dataset   dataset
     * @param gene1 the first gene
     * @param gene2 the second gene
     * @return  model
     */
    @NotNull
    Model getRelationsAsRdfV1(
        @NotNull GenePairRelationDataset dataset,
        @NotNull Identifier gene1,
        @NotNull Identifier gene2);

}
