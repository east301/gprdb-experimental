package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.pair;

import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationLoader;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class ArchivedCoexpressionLoaderFactoryImplTest {

    ///
    /// setup
    ///

    private ArchivedCoexpressionLoaderFactoryImpl factory;

    @BeforeMethod
    public void setUp() {
        this.factory = new ArchivedCoexpressionLoaderFactoryImpl();
    }

    ///
    /// newConfig
    ///

    @Test
    public void newConfig_returns_new_instance() {
        SingleFileConfig config1 = factory.newConfig();
        SingleFileConfig config2 = factory.newConfig();

        assertThat(config1).isNotNull().isNotSameAs(config2);
        assertThat(config2).isNotNull().isNotSameAs(config1);
    }

    ///
    /// newLoader
    ///

    @Test
    public void newLoader_returns_new_instance(@Mocked SingleFileConfig config) throws IOException {
        GenePairRelationLoader loader1 = factory.newLoader(config);
        GenePairRelationLoader loader2 = factory.newLoader(config);

        assertThat(loader1).isNotNull().isNotSameAs(loader2);
        assertThat(loader2).isNotNull().isNotSameAs(loader1);
    }

    @Test
    public void newLoader_returns_correctly_configured_loader(@Mocked SingleFileConfig config)
        throws IOException {

        AtomicReference<SingleFileConfig> specifiedConfig = new AtomicReference<>();
        new MockUp<ArchivedCoexpressionLoaderImpl>() {
            @Mock
            void $init(SingleFileConfig config) {
                specifiedConfig.set(config);
            }
        };

        factory.newLoader(config);
        assertThat(specifiedConfig.get()).isNotNull().isSameAs(config);
    }

}
