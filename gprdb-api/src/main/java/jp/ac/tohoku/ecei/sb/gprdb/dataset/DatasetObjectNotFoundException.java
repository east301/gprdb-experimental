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

package jp.ac.tohoku.ecei.sb.gprdb.dataset;

import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * An exception which is thrown when a specified dataset item is not found.
 *
 * @author Shu Tadaka
 */
@Getter
public class DatasetObjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String type;
    private final String id;

    public DatasetObjectNotFoundException(@NotEmpty String type, @NotEmpty String id) {
        super(String.format("%s not found: %s", type, id));
        this.type = type;
        this.id = id;
    }

    public static DatasetObjectNotFoundException identifierNotFound(@NotEmpty String id) {
        return new DatasetObjectNotFoundException("identifier", id);
    }

    public static DatasetObjectNotFoundException identifierDatasetNotFound(@NotEmpty String id) {
        return new DatasetObjectNotFoundException("identifier dataset", id);
    }

    public static DatasetObjectNotFoundException genePairRelationDatasetNotFound(@NotEmpty String id) {
        return new DatasetObjectNotFoundException("gene-pair relation dataset", id);
    }

    public static DatasetObjectNotFoundException genePairRelationTypeNotFound(@NotEmpty String id) {
        return new DatasetObjectNotFoundException("gene-pair relation type", id);
    }

}
