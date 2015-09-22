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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jp.ac.tohoku.ecei.sb.gprdb.bean.Identifiable;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.ToDoubleFunction;

/**
 * Represents a type of gene-pair relation.
 *
 * @author Shu Tadaka
 */
public interface GenePairRelationType extends Identifiable {

    /**
     * Indicates that decreasing-order is used to sort relation strength values.
     *
     * @return  {@code true} if decreasing ordering function must be applied
     *          when sorting this type of relation strength values,
     *          othwrwise {@code false}
     */
    @JsonProperty
    boolean isDecreasingOrderingUsed();

    /**
     * Gets relation types which can be combined with this relation type.
     * TODO: revise documentation
     *
     * @return  relation types which can be combined with this relation type
     */
    @NotNull
    Collection<GenePairRelationType> getShownWith();

    /**
     * Gets formatter.
     *
     * @return  formatter
     */
    @NotNull
    @JsonIgnore
    GenePairRelationStrengthFormatter getFormatter();

    /**
     * Creates a {@link Comparator} to be used when sorting this type of relation strength values.
     *
     * @param relationExtractor a function to exract relation value
     * @param <T>               type of objects to be sorted
     * @return  comparator
     */
    @NotNull
    @JsonIgnore
    <T> Comparator<T> newComparator(ToDoubleFunction<T> relationExtractor);

}
