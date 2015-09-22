package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.pair;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationLoader;
import jp.ac.tohoku.ecei.sb.gprdb.io.LoaderFactory;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author Shu Tadaka
 */
@Component
class HprdPpiLoaderFactoryImpl implements LoaderFactory<GenePairRelationLoader, SingleFileConfig> {

    @Override
    public SingleFileConfig newConfig() {
        return new SingleFileConfig();
    }

    @Override
    public GenePairRelationLoader newLoader(@NotNull SingleFileConfig config) throws IOException {
        return new HprdPpiLoaderImpl(config);
    }

}
