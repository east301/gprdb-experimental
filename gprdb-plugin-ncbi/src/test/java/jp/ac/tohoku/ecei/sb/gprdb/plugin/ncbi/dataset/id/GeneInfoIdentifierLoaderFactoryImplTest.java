package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLoader;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class GeneInfoIdentifierLoaderFactoryImplTest {

    ///
    /// newConfig
    ///

    @Test
    public void newConfig_returns_new_config_object() {
        //
        GeneInfoIdentifierLoaderFactoryImpl factory = new GeneInfoIdentifierLoaderFactoryImpl();
        Object config1 = factory.newConfig();
        Object config2 = factory.newConfig();

        //
        assertThat(config1).isNotNull().isNotSameAs(config2);
        assertThat(config2).isNotNull().isNotSameAs(config1);
    }

    ///
    /// newLoader
    ///

    @Test
    public void newLoader_returns_correct_instance(@Mocked NcbiIdentifierLoaderConfig config) {
        //
        AtomicReference<NcbiIdentifierLoaderConfig> specifiedConfig = new AtomicReference<>();
        new MockUp<GeneInfoIdentifierLoaderImpl>() {
            @Mock
            void $init(NcbiIdentifierLoaderConfig config) {
                specifiedConfig.set(config);
            }
        };

        //
        IdentifierLoader loader = new GeneInfoIdentifierLoaderFactoryImpl().newLoader(config);
        assertThat(loader).isNotNull().isInstanceOf(GeneInfoIdentifierLoaderImpl.class);
        assertThat(specifiedConfig.get()).isSameAs(config);
    }

}
