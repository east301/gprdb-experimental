package jp.ac.tohoku.ecei.sb.gprdb.dataset.id;

import org.testng.annotations.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifierMappingTypesTest {

    ///
    /// constructor
    ///

    @Test
    public void constructor_is_private() throws Exception {
        Constructor<IdentifierMappingTypes> constructor = IdentifierMappingTypes.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible()).isFalse();

        constructor.setAccessible(true);
        assertThat(constructor.newInstance()).isNotNull();
    }

}
