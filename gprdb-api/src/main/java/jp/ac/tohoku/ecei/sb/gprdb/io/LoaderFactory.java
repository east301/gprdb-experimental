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
import java.io.IOException;

/**
 * Factory of loaders.
 *
 * @author Shu Tadaka
 */
public interface LoaderFactory<T, U> {

    /**
     * Creates an new instance of configuration bean.
     *
     * @return  configuration bean
     */
    @NotNull
    U newConfig();

    /**
     * Creates an new loader instance.
     *
     * @param config    configuration
     * @return  loader
     * @throws IOException  failed to initialize loader
     */
    @NotNull
    T newLoader(@NotNull U config) throws IOException;

}
