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

import javax.validation.constraints.NotNull;
import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * An extension of loader interface of gprdb to load gene-pair relation data into internal datastore.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationLoader extends Closeable {

    /**
     * Loads genes in a dataset.
     *
     * @param callback          call back
     * @throws IOException      failed to read data from underlying data stream
     * @throws ParseException   failed to parse data from underlying data stream
     */
    void loadGenes(@NotNull Consumer<String> callback) throws IOException, ParseException;

    /**
     * Loads gene-pair relations in a dataset.
     *
     * @param callback          call back
     * @throws IOException      failed to read data from underlying data stream
     * @throws ParseException   failed to parse data from underlying data stream
     */
    void loadRelations(@NotNull Consumer<RawGenePairRelation> callback) throws IOException, ParseException;

}
