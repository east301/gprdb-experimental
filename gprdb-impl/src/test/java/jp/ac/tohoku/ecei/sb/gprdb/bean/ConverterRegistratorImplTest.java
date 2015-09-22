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

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableList;
import mockit.Delegate;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.format.FormatterRegistry;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConverterRegistratorImplTest {

    ///
    /// setup
    ///

    @Mocked
    private Converter converter1;

    @Mocked
    private Converter converter2;

    @Mocked
    private Converter converter3;

    private ConverterRegistratorImpl registrator;

    @BeforeMethod
    public void setUp() {
        new NonStrictExpectations() {{
            converter1.getSourceType(); result = String.class;
            converter1.getTargetType(); result = Integer.class;
            converter2.getSourceType(); result = Integer.class;
            converter2.getTargetType(); result = Double.class;
            converter3.getSourceType(); result = Double.class;
            converter3.getTargetType(); result = String.class;
        }};

        registrator = new ConverterRegistratorImpl(ImmutableList.of(converter1, converter2, converter3));
    }

    ///
    /// onApplicationEvent
    ///

    @Test
    public void test_onApplicationEvent() {
        //
        List<CommonsBeanutilsConverterAdapter> specifiedConverters = Lists.newArrayList();
        List<Class<?>> specifiedClasses = Lists.newArrayList();

        new MockUp<ConvertUtils>() {
            @Mock
            void register(org.apache.commons.beanutils.Converter converter, Class<?> clazz) {
                specifiedConverters.add((CommonsBeanutilsConverterAdapter)converter);
                specifiedClasses.add(clazz);
            }
        };

        //
        registrator.onApplicationEvent(null);

        assertThat(specifiedConverters).hasSize(3);
        assertThat(specifiedConverters.get(0).getConverter()).isSameAs(converter1);
        assertThat(specifiedConverters.get(1).getConverter()).isSameAs(converter2);
        assertThat(specifiedConverters.get(2).getConverter()).isSameAs(converter3);

        assertThat(specifiedClasses).hasSize(3);
        assertThat(specifiedClasses.get(0)).isEqualTo(Integer.class);
        assertThat(specifiedClasses.get(1)).isEqualTo(Double.class);
        assertThat(specifiedClasses.get(2)).isEqualTo(String.class);
    }

    ///
    /// registerFormatters
    ///

    @Test
    public void test_registerFormatters(@Mocked FormatterRegistry registry) {
        //
        List<Class<?>> specifiedSourceClasses = Lists.newArrayList();
        List<Class<?>> specifiedTargetClasses = Lists.newArrayList();
        List<SpringConverterAdapter> specifiedConverters = Lists.newArrayList();

        new NonStrictExpectations() {{
            registry.addConverter(
                (Class)any, (Class)any, (org.springframework.core.convert.converter.Converter)any);

            result = new Delegate<FormatterRegistry>() {
                void addConverter(
                    Class sourceType,
                    Class targetType,
                    org.springframework.core.convert.converter.Converter converter) {

                    specifiedSourceClasses.add(sourceType);
                    specifiedTargetClasses.add(targetType);
                    specifiedConverters.add((SpringConverterAdapter)converter);
                }
            };
        }};

        //
        registrator.registerFormatters(registry);

        assertThat(specifiedConverters).hasSize(3);
        assertThat(specifiedConverters.get(0).getConverter()).isSameAs(converter1);
        assertThat(specifiedConverters.get(1).getConverter()).isSameAs(converter2);
        assertThat(specifiedConverters.get(2).getConverter()).isSameAs(converter3);

        assertThat(specifiedSourceClasses).hasSize(3);
        assertThat(specifiedSourceClasses.get(0)).isEqualTo(String.class);
        assertThat(specifiedSourceClasses.get(1)).isEqualTo(Integer.class);
        assertThat(specifiedSourceClasses.get(2)).isEqualTo(Double.class);

        assertThat(specifiedTargetClasses).hasSize(3);
        assertThat(specifiedTargetClasses.get(0)).isEqualTo(Integer.class);
        assertThat(specifiedTargetClasses.get(1)).isEqualTo(Double.class);
        assertThat(specifiedTargetClasses.get(2)).isEqualTo(String.class);
    }

}
