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

import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.extension.Filter;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class PebbleOptionalExtensionTest {

    ///
    /// getFilters
    ///

    @Test
    public void getFilters_returns_correct_result() {
        Map<String, Filter> filters = new PebbleOptionalExtension().getFilters();
        assertThat(filters).hasSize(3).containsKeys("present", "innerValue", "orElse");
    }

    ///
    /// PresentFilter.getArgumentNames
    ///

    @Test
    public void PresentFilter_getArgumentNames_returns_correct_result() {
        assertThat(new PebbleOptionalExtension.PresentFilter().getArgumentNames()).isEmpty();
    }

    ///
    /// PresentFilter.apply
    ///

    @Test
    public void PresentFilter_apply_works_correctly_when_not_empty_value_applied() {
        Object result = new PebbleOptionalExtension.PresentFilter().apply(Optional.of("foo"), Collections.emptyMap());
        assertThat(result).isInstanceOf(Boolean.class);
        assertThat((boolean) result).isTrue();
    }

    @Test
    public void PresentFilter_apply_works_correctly_when_empty_value_applied() {
        Object result = new PebbleOptionalExtension.PresentFilter().apply(Optional.empty(), Collections.emptyMap());
        assertThat(result).isInstanceOf(Boolean.class);
        assertThat((boolean) result).isFalse();
    }

    ///
    /// InnerValueFilter.getArgumentNames
    ///

    @Test
    public void InnerValueFilter_getArgumentNames_returns_correct_result() {
        assertThat(new PebbleOptionalExtension.InnerValueFilter().getArgumentNames()).isEmpty();
    }

    ///
    /// InnerValueFilter.apply
    ///

    @Test
    public void InnerValueFilter_apply_works_correctly() {
        Object result = new PebbleOptionalExtension.InnerValueFilter().apply(Optional.of("foo"), Collections.emptyMap());
        assertThat(result).isInstanceOf(String.class).isEqualTo("foo");
    }

    ///
    /// OrElseFilter.getArgumentNames
    ///

    @Test
    public void OrElseFilter_getArgumentNames_returns_correct_result() {
        assertThat(new PebbleOptionalExtension.OrElseFilter().getArgumentNames())
            .hasSize(1)
            .contains("default");
    }

    ///
    /// OrElseFilter.apply
    ///


    @Test
    public void OrElseFilter_apply_works_correctly_when_not_empty_value_applied() {
        Object result = new PebbleOptionalExtension.OrElseFilter()
            .apply(Optional.of("foo"), ImmutableMap.of("default", "bar"));

        assertThat(result).isInstanceOf(String.class).isEqualTo("foo");
    }

    @Test
    public void OrElseFilter_apply_works_correctly_when_empty_value_applied() {
        Object result = new PebbleOptionalExtension.OrElseFilter()
            .apply(Optional.empty(), ImmutableMap.of("default", "bar"));

        assertThat(result).isInstanceOf(String.class).isEqualTo("bar");
    }

}
