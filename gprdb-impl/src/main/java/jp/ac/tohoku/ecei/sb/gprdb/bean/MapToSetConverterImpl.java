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

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author Shu Tadaka
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
class MapToSetConverterImpl implements Converter<Map, Set> {

    @Override
    public Class<Map> getSourceType() {
        return Map.class;
    }

    @Override
    public Class<Set> getTargetType() {
        return Set.class;
    }

    @Override
    public Set convert(Map source) {
        return ImmutableSet.copyOf(source.values());
    }

}