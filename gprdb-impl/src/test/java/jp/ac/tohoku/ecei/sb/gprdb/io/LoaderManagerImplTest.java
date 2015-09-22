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

package jp.ac.tohoku.ecei.sb.gprdb.io;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.springframework.beans.factory.BeanFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class LoaderManagerImplTest {

    ///
    /// setup
    ///

    @RequiredArgsConstructor
    @Getter
    public static class LoaderImpl {
        private final LoaderConfigImpl config;
    }

    @Data
    public static class LoaderConfigImpl {
        private String key;
    }

    public static class LoaderFactoryImpl implements LoaderFactory<LoaderImpl, LoaderConfigImpl> {

        @Override
        public LoaderConfigImpl newConfig() {
            return new LoaderConfigImpl();
        }

        @Override
        public LoaderImpl newLoader(@NotNull LoaderConfigImpl config) {
            return new LoaderImpl(config);
        }

    }

    @Getter
    public static class LoadableImpl implements Loadable {

        private final String loaderFactoryClass = LoaderFactoryImpl.class.getName();
        private final Map<String, Object> loaderConfig = ImmutableMap.of("key", "value");

    }

    @Mocked
    private BeanFactory beanFactory;

    private LoaderManagerImpl manager;

    @BeforeMethod
    public void setUp() {
        new NonStrictExpectations() {{
            beanFactory.getBean(LoaderFactoryImpl.class); result = new LoaderFactoryImpl();
        }};

        manager = new LoaderManagerImpl(beanFactory);
    }

    ///
    /// newLoader
    ///

    @Test
    public void testNewLoader() throws IOException {
        LoaderImpl loader = manager.newLoader(new LoadableImpl());
        assertThat(loader).isNotNull();
        assertThat(loader.getConfig()).isNotNull();
        assertThat(loader.getConfig().getKey()).isEqualTo("value");
    }

}
