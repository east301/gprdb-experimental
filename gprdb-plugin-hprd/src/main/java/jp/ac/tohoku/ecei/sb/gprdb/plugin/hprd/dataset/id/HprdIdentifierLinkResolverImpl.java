package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.id;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
class HprdIdentifierLinkResolverImpl implements IdentifierLinkResolver {

    @Override
    public Collection<String> getSupportedIdentifierPrefixes() {
        return ImmutableSet.of("hprd");
    }

    @Override
    public List<IdentifierLink> generate(@NotNull Identifier identifier) {
        return ImmutableList.of(new IdentifierLinkImpl("http://www.hprd.org/protein/" + identifier.getBody()));
    }

}
