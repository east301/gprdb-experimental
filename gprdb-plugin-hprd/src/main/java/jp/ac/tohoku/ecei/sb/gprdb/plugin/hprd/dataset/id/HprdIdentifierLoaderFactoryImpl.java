package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLoader;
import jp.ac.tohoku.ecei.sb.gprdb.io.LoaderFactory;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author Shu Tadaka
 */
@Component
class HprdIdentifierLoaderFactoryImpl implements LoaderFactory<IdentifierLoader, SingleFileConfig> {

    @Override
    public SingleFileConfig newConfig() {
        return new SingleFileConfig();
    }

    @Override
    public IdentifierLoader newLoader(@NotNull SingleFileConfig config) throws IOException {
        return new HprdIdentifierLoaderImpl(config);
    }

}
