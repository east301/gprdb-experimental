/**
 * gprdb - a simple and flexible framework for gene-pair relation database construction
 * Copyright 2015 gprdb developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jp.ac.tohoku.ecei.sb.gprdb.dataset.relation;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierDataset;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierImporter;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierInfo;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLink;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLinkResolver;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierMappingType;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import mockit.Mocked;
import mockit.Verifications;
import org.hibernate.validator.constraints.NotEmpty;
import org.openrdf.model.IRI;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
@Slf4j
public class GenePairRelationWriterImplTest {

    ///
    /// setup
    ///

    @Value
    @EqualsAndHashCode(of = "id")
    public static class IdentifierImpl implements Identifier {

        private final String id;
        private final String prefix = null;
        private final String body = null;
        private final Optional<String> name = Optional.empty();
        private final Optional<String> description = Optional.empty();
        private final Optional<IRI> uri = Optional.empty();
        private final Optional<IdentifierLink> link = Optional.empty();
        private final List<IdentifierInfo> info = Collections.emptyList();
        private final List<IdentifierLink> links = Collections.emptyList();

        @Override
        public List<Identifier> getMappingResults(@NotNull IdentifierMappingType type) {
            return Collections.emptyList();
        }

    }

    public static class IdentifierManagerImpl implements IdentifierManager {

        @Override
        public List<IdentifierDataset> getDatasets() {
            throw new RuntimeException();
        }

        @Override
        public IdentifierDataset getDataset(@NotEmpty String id) {
            throw new RuntimeException();
        }

        @Override
        public ListMultimap<String, IdentifierLinkResolver> getLinkResolvers() {
            return null;
        }

        @Override
        public Identifier wrap(@NotEmpty String id) {
            return new IdentifierImpl(id);
        }

        @Override
        public IdentifierImporter newImporter(@NotNull IdentifierDataset dataset) {
            return null;
        }

    }

    private File temporaryFile;

    @BeforeMethod
    public void setUp() throws IOException {
        temporaryFile = File.createTempFile("gprdb-test-", ".hdf5");
    }

    @AfterMethod
    public void tearDown() {
        if (this.temporaryFile.delete()) {
            log.warn("Failed to create a temporary file: {}", this.temporaryFile.getAbsolutePath());
        }
    }

    private static GenePairRelationConfig.TypeConfig typeConfig(@NotEmpty String id) {
        GenePairRelationConfig.TypeConfig config = new GenePairRelationConfig.TypeConfig();
        config.setId(id);
        return config;
    }

    ///
    /// writeMap
    ///

    @Test
    public void writeMap_writes_correct_data() {
        //
        GenePairRelationType hoge = new GenePairRelationTypeImpl(typeConfig("hoge"), null, Collections.emptyMap());
        GenePairRelationType piyo = new GenePairRelationTypeImpl(typeConfig("piyo"), null, Collections.emptyMap());
        List<GenePairRelationType> types = ImmutableList.of(hoge, piyo);

        Identifier foo = new IdentifierImpl("foo");
        Identifier bar = new IdentifierImpl("bar");
        Identifier baz = new IdentifierImpl("baz");
        List<Identifier> genes = ImmutableList.of(foo, bar, baz);

        try (IHDF5Writer hdfWriter = HDF5Factory.open(this.temporaryFile)) {
            GenePairRelationWriterImpl writer = new GenePairRelationWriterImpl(hdfWriter, types, genes);

            writer.writeMap(hoge, foo, ImmutableMap.of(foo, 1.0, bar, 2.0, baz, 3.0));
            writer.writeMap(hoge, bar, ImmutableMap.of(foo, 4.0, bar, 5.0, baz, 6.0));
            writer.writeMap(hoge, baz, ImmutableMap.of(foo, 7.0, bar, 8.0, baz, 9.0));
            writer.writeMap(piyo, foo, ImmutableMap.of(foo, -1.0, bar, -2.0, baz, -3.0));
            writer.writeMap(piyo, bar, ImmutableMap.of(foo, -4.0, bar, -5.0, baz, -6.0));
            writer.writeMap(piyo, baz, ImmutableMap.of(foo, -7.0, bar, -8.0, baz, -9.0));
        }

        //
        try (IHDF5Reader reader = HDF5Factory.openForReading(this.temporaryFile)) {
            assertThat(reader.readStringArray("/meta/genes"))
                    .hasSize(3)
                    .containsSequence("foo", "bar", "baz");
            assertThat(reader.readStringArray("/meta/types"))
                    .hasSize(2)
                    .containsSequence("hoge", "piyo");

            assertThat(reader.readDoubleArray("/genePairRelation/hoge/foo"))
                    .hasSize(3)
                    .containsSequence(1.0, 2.0, 3.0);
            assertThat(reader.readDoubleArray("/genePairRelation/hoge/bar"))
                    .hasSize(3)
                    .containsSequence(4.0, 5.0, 6.0);
            assertThat(reader.readDoubleArray("/genePairRelation/hoge/baz"))
                    .hasSize(3)
                    .containsSequence(7.0, 8.0, 9.0);

            assertThat(reader.readDoubleArray("/genePairRelation/piyo/foo"))
                    .hasSize(3)
                    .containsSequence(-1.0, -2.0, -3.0);
            assertThat(reader.readDoubleArray("/genePairRelation/piyo/bar"))
                    .hasSize(3)
                    .containsSequence(-4.0, -5.0, -6.0);
            assertThat(reader.readDoubleArray("/genePairRelation/piyo/baz"))
                    .hasSize(3)
                    .containsSequence(-7.0, -8.0, -9.0);
        }
    }

    @Test(enabled = false, expectedExceptions = Exception.class)
    public void writeMap_throws_an_exception_if_invalid_type_specified() {
        //
        GenePairRelationType hoge = new GenePairRelationTypeImpl(typeConfig("hoge"), null, Collections.emptyMap());
        GenePairRelationType piyo = new GenePairRelationTypeImpl(typeConfig("piyo"), null, Collections.emptyMap());
        GenePairRelationType invalid = new GenePairRelationTypeImpl(typeConfig("invalid"), null, Collections.emptyMap());
        List<GenePairRelationType> types = ImmutableList.of(hoge, piyo);

        Identifier foo = new IdentifierImpl("foo");
        Identifier bar = new IdentifierImpl("bar");
        Identifier baz = new IdentifierImpl("baz");
        List<Identifier> genes = ImmutableList.of(foo, bar, baz);

        try (IHDF5Writer hdfWriter = HDF5Factory.open(this.temporaryFile)) {
            GenePairRelationWriterImpl writer = new GenePairRelationWriterImpl(hdfWriter, types, genes);
            writer.writeMap(invalid, foo, ImmutableMap.of(foo, 1.0, bar, 2.0, baz, 3.0));
        }
    }

    @Test(enabled = false, expectedExceptions = Exception.class)
    public void writeMap_throws_an_exception_if_invalid_guide_gene_specified() {
        //
        GenePairRelationType hoge = new GenePairRelationTypeImpl(typeConfig("hoge"), null, Collections.emptyMap());
        GenePairRelationType piyo = new GenePairRelationTypeImpl(typeConfig("piyo"), null, Collections.emptyMap());
        List<GenePairRelationType> types = ImmutableList.of(hoge, piyo);

        Identifier foo = new IdentifierImpl("foo");
        Identifier bar = new IdentifierImpl("bar");
        Identifier baz = new IdentifierImpl("baz");
        Identifier invalid = new IdentifierImpl("invalid");
        List<Identifier> genes = ImmutableList.of(foo, bar, baz);

        try (IHDF5Writer hdfWriter = HDF5Factory.open(this.temporaryFile)) {
            GenePairRelationWriterImpl writer = new GenePairRelationWriterImpl(hdfWriter, types, genes);
            writer.writeMap(hoge, invalid, ImmutableMap.of(foo, 1.0, bar, 2.0, baz, 3.0));
        }
    }

    @Test
    public void writeMap_ignores_invalid_interacting_gene() {
        //
        GenePairRelationType hoge = new GenePairRelationTypeImpl(typeConfig("hoge"), null, Collections.emptyMap());
        GenePairRelationType piyo = new GenePairRelationTypeImpl(typeConfig("piyo"), null, Collections.emptyMap());
        List<GenePairRelationType> types = ImmutableList.of(hoge, piyo);

        Identifier foo = new IdentifierImpl("foo");
        Identifier bar = new IdentifierImpl("bar");
        Identifier baz = new IdentifierImpl("baz");
        Identifier invalid = new IdentifierImpl("invalid");
        List<Identifier> genes = ImmutableList.of(foo, bar, baz);

        try (IHDF5Writer hdfWriter = HDF5Factory.open(this.temporaryFile)) {
            GenePairRelationWriterImpl writer = new GenePairRelationWriterImpl(hdfWriter, types, genes);
            writer.writeMap(hoge, foo, ImmutableMap.of(foo, 1.0, invalid, 2.0, baz, 3.0));
        }

        //
        try (IHDF5Reader reader = HDF5Factory.openForReading(this.temporaryFile)) {
            assertThat(reader.readStringArray("/meta/genes"))
                    .hasSize(3)
                    .containsSequence("foo", "bar", "baz");
            assertThat(reader.readStringArray("/meta/types"))
                    .hasSize(2)
                    .containsSequence("hoge", "piyo");

            assertThat(reader.readDoubleArray("/genePairRelation/hoge/foo"))
                    .hasSize(3)
                    .containsSequence(1.0, Double.NaN, 3.0);
        }
    }

    ///
    /// close
    ///

    @Test
    public void close_closes_underlying_writer(
            @Mocked IHDF5Writer hdf5Writer,
            @Mocked List<GenePairRelationType> types,
            @Mocked List<Identifier> genes) {

        new GenePairRelationWriterImpl(hdf5Writer, types, genes).close();

        new Verifications() {{
            hdf5Writer.close(); times = 1;
        }};
    }

}
