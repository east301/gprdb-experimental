package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.io.LoaderFactory;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLoader;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author Shu Tadaka
 */
@Component
class GeneInfoIdentifierLoaderFactoryImpl
    implements LoaderFactory<IdentifierLoader, NcbiIdentifierLoaderConfig> {

    @Override
    public NcbiIdentifierLoaderConfig newConfig() {
        return new NcbiIdentifierLoaderConfig();
    }

    @Override
    public IdentifierLoader newLoader(@NotNull NcbiIdentifierLoaderConfig config) {
        return new GeneInfoIdentifierLoaderImpl(config);
    }

}
