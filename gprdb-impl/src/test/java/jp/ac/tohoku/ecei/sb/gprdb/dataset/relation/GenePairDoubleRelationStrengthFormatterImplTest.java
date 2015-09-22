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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class GenePairDoubleRelationStrengthFormatterImplTest {

    ///
    /// setup
    ///

    private GenePairDoubleRelationStrengthFormatterImpl formatter;

    @BeforeMethod
    public void setUp() {
        formatter = new GenePairDoubleRelationStrengthFormatterImpl();
    }

    ///
    /// toText
    ///

    @Test
    public void test_toText() {
        assertThat(formatter.toText(1)).isEqualTo("1.00");
        assertThat(formatter.toText(1.0000)).isEqualTo("1.00");
        assertThat(formatter.toText(1.2345)).isEqualTo("1.23");
    }

    ///
    /// toDecoratedText
    ///

    @Test
    public void test_toDecoratedText() {
        assertThat(formatter.toDecoratedText(1)).isEqualTo("1.00");
        assertThat(formatter.toDecoratedText(1.0000)).isEqualTo("1.00");
        assertThat(formatter.toDecoratedText(1.2345)).isEqualTo("1.23");
    }

}
