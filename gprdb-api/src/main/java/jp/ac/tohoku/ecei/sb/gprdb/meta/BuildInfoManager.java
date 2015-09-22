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

package jp.ac.tohoku.ecei.sb.gprdb.meta;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Provides gprdb's build information.
 *
 * @author Shu Tadaka
 */
public interface BuildInfoManager {

    /**
     * Gets build information.
     *
     * @return  build information
     */
    @NotNull
    BuildInfo getBuildInfo();

    /**
     * Gets dependencies.
     *
     * @return  dependencies
     */
    @NotNull
    List<DependencyLibrary> getDependencyLibraries();

}
