package jp.ac.tohoku.ecei.sb.gprdb.plugin.identifiers_dot_org.dataset.id;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.openrdf.model.impl.SimpleIRI;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifiersDotOrgIdentifierUriResolverImplTest {

    ///
    /// setup
    ///

    private IdentifiersDotOrgIdentifierUriResolverImpl resolver;

    @BeforeMethod
    public void setUp() {
        resolver = new IdentifiersDotOrgIdentifierUriResolverImpl();
    }

    ///
    /// loadMiriamNamespaces (private)
    ///

    @Test
    public void loadMiriamNamespaces_returns_empty_if_failed_to_load_file() throws IOException {
        //
        new MockUp<Resources>() {
            @Mock
            List<String> readLines(URL url, Charset charset) throws IOException {
                throw new IOException("error");
            }
        };

        //
        Set<String> result = Deencapsulation.invoke(
            IdentifiersDotOrgIdentifierUriResolverImpl.class, "loadMiriamNamespaces");

        assertThat(result).isNotNull().isEmpty();
    }

    ///
    /// getSupportedIdentifierPrefixes
    ///

    @Test
    public void test_getSupportedIdentifierPrefixes() throws IOException {
        //
        int expectedSize = Resources
            .readLines(Resources.getResource("miriam-namespaces.txt"), Charsets.UTF_8)
            .size();

        //
        assertThat(resolver.getSupportedIdentifierPrefixes())
            .isNotNull()
            .hasSize(expectedSize);
    }

    ///
    /// resolve
    ///

    @Test
    public void resolve_returns_correct_uri(@Mocked Identifier identifier) {
        new NonStrictExpectations() {{
            identifier.getId();     result = "ncbigene:12345";
            identifier.getPrefix(); result = "ncbigene";
            identifier.getBody();   result = "12345";
        }};

        assertThat(resolver.resolve(identifier))
            .isPresent()
            .contains(new SimpleIRI("http://identifiers.org/ncbigene/12345"));
    }

    @Test
    public void resolve_returns_empty_if_unsupported_identifier_passed(@Mocked Identifier identifier) {
        new NonStrictExpectations() {{
            identifier.getId();     result = "foo:12345";
            identifier.getPrefix(); result = "foo";
            identifier.getBody();   result = "12345";
        }};

        assertThat(resolver.resolve(identifier)).isEmpty();
    }

}
