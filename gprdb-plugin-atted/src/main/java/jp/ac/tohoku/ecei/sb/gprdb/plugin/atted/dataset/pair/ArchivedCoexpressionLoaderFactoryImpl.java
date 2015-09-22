package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.pair;

import jp.ac.tohoku.ecei.sb.gprdb.io.LoaderFactory;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationLoader;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author Shu Tadaka
 */
@Component
class ArchivedCoexpressionLoaderFactoryImpl
    implements LoaderFactory<GenePairRelationLoader, SingleFileConfig> {

    @Override
    public SingleFileConfig newConfig() {
        return new SingleFileConfig();
    }

    @Override
    public GenePairRelationLoader newLoader(@NotNull SingleFileConfig config)
        throws IOException {

        return new ArchivedCoexpressionLoaderImpl(config);
    }

}
