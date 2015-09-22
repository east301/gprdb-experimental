package jp.ac.tohoku.ecei.sb.gprdb.config;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.spring.PebbleViewResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.ServletContext;

/**
 * @author Shu Tadaka
 */
@Configuration
public class PebbleConfig {

    @Bean
    public ViewResolver pebbleViewResolver(
        ApplicationContext applicationContext,
        ServletContext servletContext) {

        PebbleViewResolver resolver = new PebbleViewResolver();
        resolver.setPrefix("/templates/default");
        resolver.setSuffix(".html");
        resolver.setOrder(1);
        resolver.setPebbleEngine(new PebbleEngine(new ClasspathLoader()));
        resolver.setApplicationContext(applicationContext);
        return resolver;
    }

}
