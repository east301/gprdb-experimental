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

package jp.ac.tohoku.ecei.sb.gprdb.bean;

import javax.validation.constraints.NotNull;

/**
 * Abstraction of object converter.
 *
 * @author Shu Tadaka
 */
public interface Converter<T, U> {

    /**
     * Gets source type.
     *
     * @return  source type
     */
    @NotNull
    Class<T> getSourceType();

    /**
     * Gets target type.
     *
     * @return  target type
     */
    @NotNull
    Class<U> getTargetType();

    /**
     * Performs conversion.
     *
     * @param source    source
     * @return  result
     */
    U convert(T source);

}
