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

/**
 * Creates instances of {@link GenePairRelationReader}s and {@link GenePairRelationWriter}s.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationAccessorFactory {

    /**
     * Creates an reader.
     *
     * @param id    target dataset ID
     * @return  reader
     */
    @NotNull
    GenePairRelationReader newReader(@NotNull String id);

    /**
     * Creates a writer.
     *
     * @param id    tareget dataset ID
     * @param types relation types
     * @param genes genes
     * @return  writer
     */
    @NotNull
    GenePairRelationWriter newWriter(
            @NotNull String id, @NotNull List<GenePairRelationType> types, @NotNull List<Identifier> genes);

}
