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

import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author Shu Tadaka
 */
@Component
class LoaderManagerImpl implements LoaderManager {

    private final BeanFactory beanFactory;

    @Autowired
    public LoaderManagerImpl(@NotNull BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T newLoader(@NotNull Loadable loadable) throws IOException {
        LoaderFactory<T, Object> loaderFactory = newLoaderFactory(loadable);
        Object loaderConfig = newLoaderConfig(loadable, loaderFactory);
        return loaderFactory.newLoader(loaderConfig);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T> LoaderFactory<T, Object> newLoaderFactory(@NotNull Loadable loadable) {
        Class<LoaderFactory<T, Object>> loaderFactoryClass
                = (Class<LoaderFactory<T, Object>>)Class.forName(loadable.getLoaderFactoryClass());

        return this.beanFactory.getBean(loaderFactoryClass);
    }

    @SneakyThrows
    private static Object newLoaderConfig(
            @NotNull Loadable loadable, @NotNull LoaderFactory<?, Object> loaderFactory) {

        Object loaderConfig = loaderFactory.newConfig();
        BeanUtils.copyProperties(loaderConfig, loadable.getLoaderConfig());
        return loaderConfig;
    }

}
