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

import com.beust.jcommander.internal.Lists;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class GenePairRelationTypeImplTest {

    ///
    /// setup
    ///

    @Mocked
    private GenePairRelationConfig.TypeConfig config;

    @Mocked
    private GenePairRelationConfig rootConfig;

    @Mocked
    private Map<String, GenePairRelationStrengthFormatter> formatterMap;

    private GenePairRelationTypeImpl type;

    @BeforeMethod
    public void setUp() {
        type = new GenePairRelationTypeImpl(config, rootConfig, formatterMap);
    }

    ///
    /// isDecreasingOrderingUsed
    ///

    @Test
    public void isDecreasingOrderingUsed_returns_correct_result__decreasingOrderingUsed() {
        new NonStrictExpectations() {{
            config.isDecreasingOrderingUsed(); result = true;
        }};

        assertThat(type.isDecreasingOrderingUsed()).isTrue();

        new Verifications() {{
            config.isDecreasingOrderingUsed(); times = 1;
        }};
    }

    @Test
    public void isDecreasingOrderingUsed_returns_correct_result__decreasingOrderingNotUsed() {
        new NonStrictExpectations() {{
            config.isDecreasingOrderingUsed(); result = false;
        }};

        assertThat(type.isDecreasingOrderingUsed()).isFalse();

        new Verifications() {{
            config.isDecreasingOrderingUsed(); times = 1;
        }};
    }

    ///
    /// getFormatter
    ///

    @Test
    public void getFormatter_returns_correct_result(@Mocked GenePairRelationStrengthFormatter formatter) {
        new NonStrictExpectations() {{
            config.getFormatter(); result = "foo";
            formatterMap.get("foo"); result = formatter;
        }};

        assertThat(type.getFormatter()).isSameAs(formatter);

        new Verifications() {{
            formatterMap.get("foo"); times = 1;
        }};
    }

    ///
    /// newComparator
    ///

    @Test
    public void newComparator_returns_correct_result__decreasingOrderingUsed() {
        new NonStrictExpectations() {{
            config.isDecreasingOrderingUsed(); result = true;
        }};

        List<Double> values = Lists.newArrayList(1.0, 3.0, 5.0, 6.0, 4.0, 2.0);
        values.sort(type.newComparator(v -> v));

        assertThat(values).containsSequence(6.0, 5.0, 4.0, 3.0, 2.0, 1.0);

        new Verifications() {{
            config.isDecreasingOrderingUsed(); times = 1;
        }};
    }

    @Test
    public void newComparator_returns_correct_result__decreasingOrderingNotUsed() {
        new NonStrictExpectations() {{
            config.isDecreasingOrderingUsed(); result = false;
        }};

        List<Double> values = Lists.newArrayList(1.0, 3.0, 5.0, 6.0, 4.0, 2.0);
        values.sort(type.newComparator(v -> v));

        assertThat(values).containsSequence(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);

        new Verifications() {{
            config.isDecreasingOrderingUsed(); times = 1;
        }};
    }

}
