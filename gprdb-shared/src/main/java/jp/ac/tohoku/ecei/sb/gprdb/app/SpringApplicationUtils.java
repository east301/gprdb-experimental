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

package jp.ac.tohoku.ecei.sb.gprdb.app;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.core.annotation.Order;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Utility functions to manipulate {@link SpringApplication}.
 *
 * @author Shu Tadaka
 */
@Slf4j
public final class SpringApplicationUtils {

    private static final String APPLICATION_SOURCE_PROVIDER_CLASS
        = "jp.ac.tohoku.ecei.sb.gprdb.app.ApplicationSourceProvider";

    private SpringApplicationUtils() {
        // do nothing
    }

    /**
     * Creates a new instance of {@link SpringApplication}.
     *
     * @return  spring application
     */
    @SuppressWarnings("unchecked")
    public static SpringApplication newApplication() {
        //
        List<Object> applicationSources = Lists.newArrayList();

        try {
            //
            Class<?> applicationSourceProviderClass = Class.forName(APPLICATION_SOURCE_PROVIDER_CLASS);
            Method getSourcesMethod = applicationSourceProviderClass.getDeclaredMethod("getSources");

            //
            ServiceLoader<?> loader = newServiceLoader(applicationSourceProviderClass);
            for (Object sourceProvider : ImmutableList.copyOf(loader.iterator())) {
                applicationSources.addAll((Collection<Object>)getSourcesMethod.invoke(sourceProvider));
            }
        } catch (Exception ex) {
            log.warn("Failed to load application sources", ex);
        }

        //
        if (applicationSources.isEmpty()) {
            log.warn("no application source found, using fallback. some functionalities may work properly");
            applicationSources.add(FallbackApplicationSource.class);
        } else {
            applicationSources.sort(Comparator.comparing(SpringApplicationUtils::getOrder));
        }

        //
        return new SpringApplication(applicationSources.toArray());
    }

    private static <T> ServiceLoader<T> newServiceLoader(@NotNull Class<T> clazz) {
        return ServiceLoader.load(clazz);
    }

    private static int getOrder(@NotNull Object object) {   // NOSONAR
        Order order = object.getClass().getDeclaredAnnotation(Order.class);
        return order != null ? order.value() : 10;
    }

}
