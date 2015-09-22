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
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Wraps {@link GenePairRelationReader} not to be closed by {@link GenePairRelationReader#close()}
 * to cache instances for performance reasons. Instead of using {@link GenePairRelationReader#close()},
 * underlying reader implementation can be closed by {@link GenePairRelationReaderCacheWrapper#dispose()}.
 *
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class GenePairRelationReaderCacheWrapper implements GenePairRelationReader {

    private final GenePairRelationReader parent;

    @Override
    public List<Identifier> getGenes() {
        return this.parent.getGenes();
    }

    @Override
    public boolean containsGene(@NotNull Identifier gene) {
        return this.parent.containsGene(gene);
    }

    @Override
    public Map<Identifier, Double> readMap(@NotNull GenePairRelationType type, @NotNull Identifier guideGene) {
        return this.parent.readMap(type, guideGene);
    }

    @Override
    public void close() {
        // do nothing
    }

    public void dispose() {
        this.parent.close();
    }

}
