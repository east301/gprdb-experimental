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

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring.PebbleViewResolver;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.ServletContext;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Configures pebble.
 *
 * @author Shu Tadaka
 */
@Configuration
@ConditionalOnWebApplication
public class PebbleConfig {

    @Bean
    public Loader templateLoader(@NotNull ServletContext context) {
        return new ServletLoader(context);
    }

    @Bean
    public PebbleEngine pebbleEngine(@NotNull Loader loader, @NotNull List<Extension> extensions) {
        PebbleEngine engine = new PebbleEngine(loader);
        for (Extension extension : extensions) {
            engine.addExtension(extension);
        }

        return engine;
    }

    @Bean
    @ConditionalOnProperty("gprdb.template.theme")
    public ViewResolver customThemeViewResolver(
        @NotNull ApplicationContext applicationContext,
        @NotNull PebbleEngine engine,
        @Value("${gprdb.template.theme}") String templateTheme) {

        return newViewResolver(applicationContext, engine, templateTheme, 5);
    }

    @Bean
    public ViewResolver defaultThemeViewResolver(
        @NotNull ApplicationContext applicationContext,
        @NotNull PebbleEngine engine) {

        return newViewResolver(applicationContext, engine, "default", 10);
    }

    private static ViewResolver newViewResolver(
        @NotNull ApplicationContext applicationContext,
        @NotNull PebbleEngine engine,
        @NotEmpty String theme,
        @Min(0) int order) {

        PebbleViewResolver resolver = new PebbleViewResolver();
        resolver.setPrefix("/templates/" + theme + "/");
        resolver.setSuffix(".html");
        resolver.setOrder(order);
        resolver.setPebbleEngine(engine);
        resolver.setApplicationContext(applicationContext);
        return resolver;
    }

}
