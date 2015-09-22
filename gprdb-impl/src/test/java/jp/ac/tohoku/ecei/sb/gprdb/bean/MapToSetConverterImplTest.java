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

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class MapToSetConverterImplTest {

    ///
    /// getSourceType
    ///

    public void test_getSourceType() {
        assertThat(new MapToSetConverterImpl().getSourceType()).isEqualTo(Map.class);
    }

    ///
    /// getTargetType
    ///

    public void test_getTargetType() {
        assertThat(new MapToSetConverterImpl().getTargetType()).isEqualTo(Set.class);
    }

    ///
    /// convert
    ///

    @Test
    @SuppressWarnings("unchecked")
    public void convert_returns_correct_result() {
        Set<String> result = new MapToSetConverterImpl()
            .convert(ImmutableMap.of("1", "foo", "2", "bar", "3", "baz"));

        assertThat(result).hasSize(3).contains("foo", "bar", "baz");
    }

}
