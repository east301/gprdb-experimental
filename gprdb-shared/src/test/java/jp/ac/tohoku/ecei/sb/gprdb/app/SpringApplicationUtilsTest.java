package jp.ac.tohoku.ecei.sb.gprdb.app;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.springframework.boot.SpringApplication;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class SpringApplicationUtilsTest {

    ///
    /// constructor
    ///

    @Test
    public void constructor_is_private() throws Exception {
        Constructor<SpringApplicationUtils> constructor = SpringApplicationUtils.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible()).isFalse();

        constructor.setAccessible(true);
        assertThat(constructor.newInstance()).isNotNull();
    }

    ///
    /// newApplication
    ///

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void newApplication_uses_fallback_source_if_no_source_found(@Mocked ServiceLoader serviceLoader) {
        //
        AtomicReference<Object[]> specifiedSources = new AtomicReference<>();
        new MockUp<SpringApplication>() {
            @Mock
            void $init(Object[] sources) {
                specifiedSources.set(sources);
            }
        };

        new MockUp<SpringApplicationUtils>() {
            @Mock
            <T> ServiceLoader<T> newServiceLoader(@NotNull Class<T> clazz) {
                return (ServiceLoader<T>)serviceLoader;
            }
        };

        new NonStrictExpectations() {{
            serviceLoader.iterator(); result = Collections.emptyList();
        }};

        //
        assertThat(SpringApplicationUtils.newApplication()).isNotNull();
        assertThat(specifiedSources.get()).isNotNull().hasSize(1).contains(FallbackApplicationSource.class);
    }

}
