package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class HomoloGeneIdentifierLoaderFactoryImplTest {

    ///
    /// setup
    ///

    private HomoloGeneIdentifierLoaderFactoryImpl factory;

    @BeforeMethod
    public void setUp() {
        factory = new HomoloGeneIdentifierLoaderFactoryImpl();
    }

    ///
    /// newConfig
    ///

    @Test
    public void test_newConfig() {
        NcbiIdentifierLoaderConfig config1 = factory.newConfig();
        NcbiIdentifierLoaderConfig config2 = factory.newConfig();
        assertThat(config1).isNotNull().isNotSameAs(config2);
        assertThat(config2).isNotNull().isNotSameAs(config1);
    }

    ///
    /// newLoader
    ///

    @Test
    public void test_newLoader(@Mocked NcbiIdentifierLoaderConfig config) {
        AtomicReference<NcbiIdentifierLoaderConfig> specifiedConfig = new AtomicReference<>();
        new MockUp<HomoloGeneIdentifierLoaderImpl>() {
            @Mock
            void $init(@NotNull NcbiIdentifierLoaderConfig config) {
                specifiedConfig.set(config);
            }
        };

        assertThat(factory.newLoader(config)).isNotNull();
        assertThat(specifiedConfig.get()).isNotNull().isSameAs(config);
    }

}
