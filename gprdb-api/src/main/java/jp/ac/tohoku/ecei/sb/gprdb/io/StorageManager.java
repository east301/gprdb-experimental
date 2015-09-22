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

package jp.ac.tohoku.ecei.sb.gprdb.io;

import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * Manages persistent storages.
 *
 * @author Shu Tadaka
 */
public interface StorageManager {

    /**
     * Gets a file.
     *
     * @param head  head of path
     * @param tail  tail of path
     * @return  file
     */
    @NotNull
    File getFile(@NotNull String head, String... tail);

    /**
     * Indicates whether databases should be opened in read-only mode or not.
     *
     * @return  {@code true} if databases should be opened in read-only mode, otherwise {@code false}
     */
    boolean isReadOnlyModePreferred();

}
