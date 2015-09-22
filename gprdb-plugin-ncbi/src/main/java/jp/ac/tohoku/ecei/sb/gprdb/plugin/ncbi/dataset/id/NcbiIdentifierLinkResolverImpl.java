package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import com.google.common.collect.ImmutableList;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLink;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLinkResolver;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author Shu Tadaka
 */
@Component
class NcbiIdentifierLinkResolverImpl implements IdentifierLinkResolver {

    @Override
    public Collection<String> getSupportedIdentifierPrefixes() {
        return ImmutableList.of("ncbigene");
    }

    @Override
    public List<IdentifierLink> generate(@NotNull Identifier identifier) {
        return ImmutableList.of(
            new NcbiIdentifierLinkImpl("http://www.ncbi.nlm.nih.gov/gene/" + identifier.getBody())
        );
    }

}
