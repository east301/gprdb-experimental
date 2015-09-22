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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * @author Shu Tadaka
 */
class GenePairRelationTypeImpl
        extends AbstractIdentifiableImpl<GenePairRelationConfig.TypeConfig>
        implements GenePairRelationType {

    private final GenePairRelationConfig rootConfig;
    private final Map<String, GenePairRelationStrengthFormatter> formatterMap;

    public GenePairRelationTypeImpl(
        @NotNull GenePairRelationConfig.TypeConfig config,
        @NotNull GenePairRelationConfig rootConfig,
        @NotNull Map<String, GenePairRelationStrengthFormatter> formatterMap) {

        super(config);
        this.rootConfig = rootConfig;
        this.formatterMap = formatterMap;
    }

    @Override
    public boolean isDecreasingOrderingUsed() {
        return this.config.isDecreasingOrderingUsed();
    }

    @Override
    public Collection<GenePairRelationType> getShownWith() {
        //
        Map<String, GenePairRelationConfig.TypeConfig> typeConfigMap = Maps.uniqueIndex(
            this.rootConfig.getTypes(), GenePairRelationConfig.TypeConfig::getId);

        //
        return Lists.transform(
            this.config.getShownWith(),
            id -> new GenePairRelationTypeImpl(typeConfigMap.get(id), this.rootConfig, this.formatterMap));
    }

    @Override
    public GenePairRelationStrengthFormatter getFormatter() {
        return this.formatterMap.get(this.config.getFormatter());
    }

    @Override
    public <T> Comparator<T> newComparator(ToDoubleFunction<T> relationExtractor) {
        Comparator<T> comparator =Comparator.comparingDouble(relationExtractor);
        if (this.config.isDecreasingOrderingUsed()) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}
