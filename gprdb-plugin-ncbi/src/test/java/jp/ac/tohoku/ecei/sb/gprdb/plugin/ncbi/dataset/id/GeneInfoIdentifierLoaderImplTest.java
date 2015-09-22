package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierMappingTypes;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierInfo;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierMapping;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.hibernate.validator.constraints.NotEmpty;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
@Slf4j
public class GeneInfoIdentifierLoaderImplTest {

    ///
    /// setup
    ///

    @RequiredArgsConstructor
    @Getter
    private static class Fixture {
        private final String name;
        private final String[][] lines;

        private File file;

        @SneakyThrows
        public void prepare() {
            this.file = File.createTempFile("gprdb-", ".txt");
            try (OutputStream ofs = new FileOutputStream(this.file)) {
                Resources.copy(Resources.getResource(GeneInfoIdentifierLoaderImplTest.class, this.name), ofs);
            }
        }

        public void cleanup() {
            if (!this.file.delete()) {
                log.warn("failed to delete a file: {}", this.file.getAbsolutePath());
            }
        }

    }

    private final Fixture[] fixtures = {
        new Fixture(
            "gene-info-normal.txt",
            new String[][]{
                {"ncbigene:1246500", "repA1", "putative replication-associated protein"},
                {"ncbigene:1246501", "repA2", "putative replication-associated protein"},
                {"ncbigene:1246502", "leuA", "2-isopropylmalate synthase"},
                {"ncbigene:5961931", "pMF1.19c", "hypothetical protein"},
            }
        ),
        new Fixture(
            "gene-info-cross-reference.txt",
            new String[][]{
                {"ncbigene:814629", "AT2G01050", "zinc ion binding / nucleic acid binding protein"}
            }
        ),
        new Fixture(
            "gene-info-no-description.txt",
            new String[][]{
                {"ncbigene:1246500", "repA1", null},
            }
        ),
        new Fixture(
            "gene-info-no-symbol-no-description.txt",
            new String[][]{
                {"ncbigene:1246500", null, null},
            }
        ),
    };

    @Mocked
    private NcbiIdentifierLoaderConfig config;

    private GeneInfoIdentifierLoaderImpl loader;

    @BeforeClass
    public void beforeClass() throws IOException {
        for (Fixture fixture : fixtures) {
            fixture.prepare();
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        loader = new GeneInfoIdentifierLoaderImpl(config);
    }

    @AfterClass
    public void afterClass() {
        for (Fixture fixture : fixtures) {
            fixture.cleanup();
        }
    }

    ///
    /// load
    ///

    @Test
    public void load_returns_correct_data__no_target_taxonomy_id() throws IOException, ParseException {
        //
        Fixture fixture = fixtures[0];
        new NonStrictExpectations() {{
            config.getPath(); result = fixture.getFile().getAbsolutePath();
        }};

        //
        List<RawIdentifierInfo> ids = Lists.newArrayList();
        loader.load(ids::add);

        assertThat(ids).hasSize(fixture.getLines().length);
        verifyIdentifier(ids.get(0), fixture.getLines()[0]);
        verifyIdentifier(ids.get(1), fixture.getLines()[1]);
        verifyIdentifier(ids.get(2), fixture.getLines()[2]);
        verifyIdentifier(ids.get(3), fixture.getLines()[3]);
    }

    @Test
    public void load_returns_correct_data__multiple_taxonomy_ids() throws IOException, ParseException {
        Fixture fixture = fixtures[0];
        new NonStrictExpectations() {{
            config.getPath(); result = fixture.getFile().getAbsolutePath();
            config.getTargetTaxonomyIds(); result = ImmutableSet.of("9", "33");
        }};

        List<RawIdentifierInfo> ids = Lists.newArrayList();
        loader.load(ids::add);

        assertThat(ids).hasSize(fixture.getLines().length);
        verifyIdentifier(ids.get(0), fixture.getLines()[0]);
        verifyIdentifier(ids.get(1), fixture.getLines()[1]);
        verifyIdentifier(ids.get(2), fixture.getLines()[2]);
        verifyIdentifier(ids.get(3), fixture.getLines()[3]);
    }

    @Test
    public void load_returns_correct_data__single_taxonomy_id__001() throws IOException, ParseException {
        Fixture fixture = fixtures[0];
        new NonStrictExpectations() {{
            config.getPath(); result = fixture.getFile().getAbsolutePath();
            config.getTargetTaxonomyIds(); result = ImmutableSet.of("9");
        }};

        List<RawIdentifierInfo> ids = Lists.newArrayList();
        loader.load(ids::add);

        assertThat(ids).hasSize(3);
        verifyIdentifier(ids.get(0), fixture.getLines()[0]);
        verifyIdentifier(ids.get(1), fixture.getLines()[1]);
        verifyIdentifier(ids.get(2), fixture.getLines()[2]);
    }

    @Test
    public void load_returns_correct_data__single_taxonomy_id__002() throws IOException, ParseException {
        Fixture fixture = fixtures[0];
        new NonStrictExpectations() {{
            config.getPath(); result = fixture.getFile().getAbsolutePath();
            config.getTargetTaxonomyIds(); result = ImmutableSet.of("33");
        }};

        List<RawIdentifierInfo> ids = Lists.newArrayList();
        loader.load(ids::add);

        assertThat(ids).hasSize(1);
        verifyIdentifier(ids.get(0), fixture.getLines()[3]);
    }

    @Test
    public void load_correctly_parses_cross_references() throws IOException, ParseException {
        Fixture fixture = fixtures[1];
        new NonStrictExpectations() {{
            config.getPath(); result = fixture.getFile().getAbsolutePath();
        }};

        List<RawIdentifierInfo> ids = Lists.newArrayList();
        loader.load(ids::add);

        assertThat(ids).hasSize(1);
        assertThat(ids.get(0).getMappings()).hasSize(1);

        RawIdentifierMapping mapping = ids.get(0).getMappings().iterator().next();
        assertThat(mapping.getType()).isEqualTo(IdentifierMappingTypes.INTRA_SPECIES);
        assertThat(mapping.getHint()).isEmpty();
        assertThat(mapping.getIdentifier()).isEqualTo("tair.locus:AT2G01050");
    }

    @Test
    public void load_correctly_parses_no_description_entry() throws IOException, ParseException {
        Fixture fixture = fixtures[2];
        new NonStrictExpectations() {{
            config.getPath(); result = fixture.getFile().getAbsolutePath();
        }};

        List<RawIdentifierInfo> ids = Lists.newArrayList();
        loader.load(ids::add);

        assertThat(ids).hasSize(1);
        verifyIdentifier(ids.get(0), fixture.getLines()[0]);
    }

    @Test
    public void load_correctly_parses_no_symbol_no_description_entry() throws IOException, ParseException {
        Fixture fixture = fixtures[3];
        new NonStrictExpectations() {{
            config.getPath(); result = fixture.getFile().getAbsolutePath();
        }};

        List<RawIdentifierInfo> ids = Lists.newArrayList();
        loader.load(ids::add);

        assertThat(ids).isEmpty();
    }

    private void verifyIdentifier(
        @NotNull RawIdentifierInfo identifier, @NotEmpty String[] expectedEntry) {

        assertThat(identifier.getId()).isEqualTo(expectedEntry[0]);

        if (expectedEntry[1] != null) {
            assertThat(identifier.getName()).isPresent().contains(expectedEntry[1]);
        } else {
            assertThat(identifier.getName()).isEmpty();
        }

        if (expectedEntry[2] != null) {
            assertThat(identifier.getDescription()).isPresent().contains(expectedEntry[2]);
        } else {
            assertThat(identifier.getDescription()).isEmpty();
        }

        assertThat(identifier.getMappings()).isEmpty();
    }

    ///
    /// close
    ///

    @Test
    public void close_do_nothing__no_exception_thrown() {
        loader.close();
    }

}
