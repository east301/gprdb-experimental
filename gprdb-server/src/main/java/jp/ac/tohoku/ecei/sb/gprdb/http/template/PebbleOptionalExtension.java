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

package jp.ac.tohoku.ecei.sb.gprdb.http.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A Pebble extension to add several filters for manipulation of {@link Optional}s.
 *
 * @author Shu Tadaka
 */
@Component
@ConditionalOnWebApplication
class PebbleOptionalExtension extends AbstractExtension {

    @Override
    public Map<String, Filter> getFilters() {
        return ImmutableMap.of(
            "present", new PresentFilter(),
            "innerValue", new InnerValueFilter(),
            "orElse", new OrElseFilter()
        );
    }

    private abstract static class AbstractNoArgFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return Collections.emptyList();
        }

    }

    public static class PresentFilter extends AbstractNoArgFilter {

        @Override
        @SuppressWarnings("unchecked")
        public Object apply(Object input, Map<String, Object> args) {
            return ((Optional<?>)input).isPresent();
        }

    }

    public static class InnerValueFilter extends AbstractNoArgFilter {

        @Override
        @SuppressWarnings("unchecked")
        public Object apply(Object input, Map<String, Object> args) {
            return ((Optional<?>)input).get();
        }

    }

    public static class OrElseFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("default");
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object apply(Object input, Map<String, Object> args) {
            return ((Optional<Object>)input).orElse(args.get("default"));
        }

    }

}
