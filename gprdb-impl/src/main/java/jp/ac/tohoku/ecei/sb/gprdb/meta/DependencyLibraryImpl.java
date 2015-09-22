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

import lombok.Data;

import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Data
class DependencyLibraryImpl implements DependencyLibrary {

    private String name;
    private Optional<String> description = Optional.empty();
    private Optional<String> version = Optional.empty();
    private Optional<String> projectUrl = Optional.empty();
    private Optional<String> license = Optional.empty();
    private Optional<String> licenseUrl = Optional.empty();

}