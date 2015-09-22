package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.pair;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.common.io.Resources;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.RawGenePairRelation;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import lombok.extern.slf4j.Slf4j;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
@Slf4j
public class ArchivedCoexpressionLoaderImplTest {

    ///
    /// setup
    ///

    @Mocked
    private SingleFileConfig config;

    private ArchivedCoexpressionLoaderImpl loader;

    private String[] fileNames = {
        "coexpression-normal.tar.bz2",
        "coexpression-normal.tbz2",
        "coexpression-normal.tar.gz",
        "coexpression-normal.tgz",
        "coexpression-normal.zip",
        "coexpression-normal.7z",
        "coexpression-normal.foo",
    };

    private Map<String, File> files;

    @BeforeClass
    public void beforeClass() throws IOException {
        files = Maps.newHashMap();
        for (String name : fileNames) {
            File dest = File.createTempFile("gprdb-", name);
            try (OutputStream fout = new FileOutputStream(dest)) {
                Resources.copy(Resources.getResource(ArchivedCoexpressionLoaderImplTest.class, name), fout);
            }

            files.put(name, dest);
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        loader = new ArchivedCoexpressionLoaderImpl(config);
    }

    @AfterClass
    public void afterClass() throws Exception {
        for (File file : files.values()) {
            if (!file.delete()) {
                log.warn("failed to delete a temporary file: {}", file.getAbsolutePath());
            }
        }
    }

    ///
    /// loadGenes
    ///

    @Test
    public void loadGenes_returns_correct_result() throws Exception {
        //
        new NonStrictExpectations() {{
            config.getPath(); result = files.get("coexpression-normal.tar.bz2");
        }};

        //
        List<String> genes = Lists.newArrayList();
        loader.loadGenes(genes::add);

        assertThat(genes).hasSize(3).containsSequence("ncbigene:1", "ncbigene:2", "ncbigene:3");
    }

    ///
    /// loadRelations
    ///

    @DataProvider
    public Object[][] loadRelations_retrurns_correct_relations__extensions() {
        return new Object[][]{
            {".tar.bz2"}, {".tbz2"}, {".tar.gz"}, {".tgz"}, {".zip"}, {".7z"}
        };
    }

    @Test(dataProvider = "loadRelations_retrurns_correct_relations__extensions")
    public void loadRelations_returns_correct_relations(String extension) throws Exception {
        //
        new NonStrictExpectations() {{
            config.getPath(); result = files.get("coexpression-normal" + extension);
        }};

        //
        List<RawGenePairRelation> relations = Lists.newArrayList();
        loader.loadRelations(relations::add);

        assertThat(relations).hasSize(6);

        assertThat(relations.get(0).getGuideGene()).isEqualTo("ncbigene:1");
        assertThat(relations.get(0).getRelationType()).isEqualTo("mr");
        assertThat(relations.get(0).getRelationStrengthMap())
            .hasSize(3)
            .containsEntry("ncbigene:1", 1.0)
            .containsEntry("ncbigene:2", 2.0)
            .containsEntry("ncbigene:3", 3.0);

        assertThat(relations.get(1).getGuideGene()).isEqualTo("ncbigene:1");
        assertThat(relations.get(1).getRelationType()).isEqualTo("pcc");
        assertThat(relations.get(1).getRelationStrengthMap())
            .hasSize(3)
            .containsEntry("ncbigene:1", 4.0)
            .containsEntry("ncbigene:2", 5.0)
            .containsEntry("ncbigene:3", 6.0);

        assertThat(relations.get(2).getGuideGene()).isEqualTo("ncbigene:2");
        assertThat(relations.get(2).getRelationType()).isEqualTo("mr");
        assertThat(relations.get(2).getRelationStrengthMap())
            .hasSize(3)
            .containsEntry("ncbigene:1", 7.0)
            .containsEntry("ncbigene:2", 8.0)
            .containsEntry("ncbigene:3", 9.0);

        assertThat(relations.get(3).getGuideGene()).isEqualTo("ncbigene:2");
        assertThat(relations.get(3).getRelationType()).isEqualTo("pcc");
        assertThat(relations.get(3).getRelationStrengthMap())
            .hasSize(3)
            .containsEntry("ncbigene:1", 10.0)
            .containsEntry("ncbigene:2", 11.0)
            .containsEntry("ncbigene:3", 12.0);

        assertThat(relations.get(4).getGuideGene()).isEqualTo("ncbigene:3");
        assertThat(relations.get(4).getRelationType()).isEqualTo("mr");
        assertThat(relations.get(4).getRelationStrengthMap())
            .hasSize(3)
            .containsEntry("ncbigene:1", 13.0)
            .containsEntry("ncbigene:2", 14.0)
            .containsEntry("ncbigene:3", 15.0);

        assertThat(relations.get(5).getGuideGene()).isEqualTo("ncbigene:3");
        assertThat(relations.get(5).getRelationType()).isEqualTo("pcc");
        assertThat(relations.get(5).getRelationStrengthMap())
            .hasSize(3)
            .containsEntry("ncbigene:1", 16.0)
            .containsEntry("ncbigene:2", 17.0)
            .containsEntry("ncbigene:3", 18.0);
    }

    @Test(
        expectedExceptions = IllegalStateException.class,
        expectedExceptionsMessageRegExp = "cannot detect compression format from file extension: .*"
    )
    public void loadRelations_throws_an_exception_if_invalid_extension_specified() throws Exception {
        //
        new NonStrictExpectations() {{
            config.getPath(); result = files.get("coexpression-normal.foo");
        }};

        //
        loader.loadRelations(t -> {});
    }

    ///
    /// close
    ///

    @Test
    public void close_throws_no_exception() {
        loader.close();
    }

}
