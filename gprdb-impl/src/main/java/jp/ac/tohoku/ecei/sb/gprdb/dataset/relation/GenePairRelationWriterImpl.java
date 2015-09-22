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

import ch.systemsx.cisd.hdf5.IHDF5Writer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Shu Tadaka
 */
class GenePairRelationWriterImpl implements GenePairRelationWriter {

    private final IHDF5Writer writer;
    private final List<Identifier> genes;

    public GenePairRelationWriterImpl(
            @NotNull IHDF5Writer writer,
            @NotNull List<GenePairRelationType> relationTypes,      // TODO
            @NotNull List<Identifier> genes) {

        //
        this.writer = writer;
        this.genes = genes;

        //
        this.writer.writeStringArray(
                "/meta/types",
                extractIds(relationTypes, GenePairRelationType::getId));
        this.writer.writeStringArray(
            "/meta/genes",
            extractIds(genes, Identifier::getId));
    }

    private static <T> String[] extractIds(@NotNull List<T> items, @NotNull Function<T, String> extractor) {
        return Lists.transform(items, extractor).toArray(new String[0]);
    }

    @Override
    public void writeMap(
            @NotNull GenePairRelationType type,
            @NotNull Identifier guideGene,
            @NotNull Map<Identifier, Double> values) {

        //
        double[] row = new double[this.genes.size()];
        int index = 0;

        for (Identifier gene : this.genes) {
            row[index++] = values.getOrDefault(gene, Double.NaN);
        }

        //
        String key = "/genePairRelation/" + type.getId() + "/" + guideGene.getId();
        this.writer.writeDoubleArray(key, row);
    }

    @Override
    public void close() {
        this.writer.close();
    }

}
