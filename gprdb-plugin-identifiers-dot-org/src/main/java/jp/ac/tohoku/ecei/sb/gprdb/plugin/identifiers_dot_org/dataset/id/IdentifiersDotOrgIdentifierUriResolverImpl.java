package jp.ac.tohoku.ecei.sb.gprdb.plugin.identifiers_dot_org.dataset.id;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierUriResolver;
import lombok.extern.slf4j.Slf4j;
import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleIRI;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * An implementation of {@link IdentifierUriResolver} which returns URIs
 * issued by <a href="http://identifiers.org">identifiers.org</a>.
 *
 * Supported identifier types (prefixes) are listed in {@code miriam-namespaces.txt}
 * which is embedded as a resource. {@code miriam-namespaces.txt} can be update by running
 * {@code ./gradlew :gprdb-plugin-identifiers-dot-org:updateSupportedIdentifierPrefixList}.
 * In the update process, latest MIRIAM dataset is downloaded and {@code miriam-namespaces.txt} is generated.
 *
 * @author Shu Tadaka
 */
@Component
@Slf4j
class IdentifiersDotOrgIdentifierUriResolverImpl implements IdentifierUriResolver {

    private final Set<String> supportedIdentifierPrefixes = loadMiriamNamespaces();

    private static Set<String> loadMiriamNamespaces() {
        try {
            URL url = Resources.getResource("miriam-namespaces.txt");
            return ImmutableSet.copyOf(Resources.readLines(url, Charsets.UTF_8));
        } catch (IOException ex) {
            log.error("failed to load miriam-namespaces.txt", ex);
            return Collections.emptySet();
        }
    }

    @Override
    public Collection<String> getSupportedIdentifierPrefixes() {
        return supportedIdentifierPrefixes;
    }

    @Override
    public Optional<IRI> resolve(@NotNull Identifier id) {
        return this.supportedIdentifierPrefixes.contains(id.getPrefix())
            ? Optional.of(new SimpleIRI("http://identifiers.org/" + id.getPrefix() + "/" + id.getBody()))
            : Optional.empty();
    }

}
