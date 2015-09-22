package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLink;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class CoxpresdbIdentifierLinkResolverImplTest {

    ///
    /// setup
    ///

    private CoxpresdbIdentifierLinkResolverImpl generator;

    @BeforeMethod
    public void setUp() {
        generator = new CoxpresdbIdentifierLinkResolverImpl();
    }

    ///
    /// getSupportedIdentifierPrefixes
    ///

    @Test
    public void test_getSupportedIdentifierPrefixes() {
        assertThat(generator.getSupportedIdentifierPrefixes())
            .hasSize(1)
            .contains("ncbigene");
    }

    ///
    /// generate
    ///

    @Test
    public void test_generate(@Mocked Identifier identifier) {
        new NonStrictExpectations() {{
            identifier.getBody(); result = "123456";
        }};

        List<IdentifierLink> links = generator.generate(identifier);
        assertThat(links).hasSize(1);
        assertThat(links.get(0).getUrl()).isEqualTo("http://coxpresdb.jp/data/gene/123456.shtml");
        assertThat(links.get(0).getLogoUrl()).isPresent().contains("/static/gprdb-plugin-atted/coxpresdb-logo-32px.png");

        new Verifications() {{
            identifier.getBody(); times = 1;
        }};
    }

}
