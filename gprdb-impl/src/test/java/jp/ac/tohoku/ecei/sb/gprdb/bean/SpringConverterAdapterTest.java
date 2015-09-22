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

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class SpringConverterAdapterTest {

    ///
    /// convert
    ///

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void test_convert(@Mocked Converter wrapped) {
        new NonStrictExpectations() {{
            wrapped.convert("foo"); result = "bar";
        }};

        SpringConverterAdapter adapter = new SpringConverterAdapter(wrapped);
        assertThat(adapter.convert("foo")).isEqualTo("bar");

        new Verifications() {{
            wrapped.convert("foo"); times = 1;
        }};
    }

}
