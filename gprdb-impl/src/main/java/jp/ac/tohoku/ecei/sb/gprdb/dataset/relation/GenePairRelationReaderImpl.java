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

import ch.systemsx.cisd.hdf5.IHDF5Reader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Shu Tadaka
 */
class GenePairRelationReaderImpl implements GenePairRelationReader {

    private final IHDF5Reader reader;
    private final IdentifierManager identifierManager;
    private final String[] geneIds;

    public GenePairRelationReaderImpl(@NotNull IHDF5Reader reader, @NotNull IdentifierManager identifierManager) {
        this.identifierManager = identifierManager;
        this.reader = reader;
        this.geneIds = reader.readStringArray("/meta/genes");
    }

    @Override
    public List<Identifier> getGenes() {
        return Lists.transform(Arrays.asList(this.geneIds), this.identifierManager::wrap);
    }

    @Override
    public boolean containsGene(@NotNull Identifier gene) {
        return Arrays.binarySearch(this.geneIds, gene.getId()) >= 0;
    }

    @Override
    public Map<Identifier, Double> readMap(@NotNull GenePairRelationType type, @NotNull Identifier guideGene) {
        //
        String key = "/genePairRelation/" + type.getId() + "/" + guideGene.getId();
        double[] row = this.reader.readDoubleArray(key);

        //
        Map<Identifier, Double> result = Maps.newHashMap();
        for (int i = 0; i < this.geneIds.length; i++) {
            if (!Double.isNaN(row[i])) {
                result.put(this.identifierManager.wrap(this.geneIds[i]), row[i]);
            }
        }

        return result;
    }

    @Override
    public void close() {
        this.reader.close();
    }

}
