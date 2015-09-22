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

import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Shu Tadaka
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ConverterRegistratorImpl implements ApplicationListener<ContextRefreshedEvent>, FormatterRegistrar {

    private final List<Converter<?, ?>> converters;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // registers converters to Commons Beanutils
        for (Converter<?, ?> converter : this.converters) {
            ConvertUtils.register(new CommonsBeanutilsConverterAdapter(converter), converter.getTargetType());
        }
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        // registers converters to Spring container
        for (Converter<?, ?> converter : this.converters) {
            registry.addConverter(
                converter.getSourceType(), converter.getTargetType(), new SpringConverterAdapter(converter));
        }
    }

}
