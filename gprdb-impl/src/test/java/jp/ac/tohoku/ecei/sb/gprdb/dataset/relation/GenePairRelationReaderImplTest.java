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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleFunction;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
@Slf4j
public class GenePairRelationReaderImplTest {

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

    @Value
    @EqualsAndHashCode(of = "id")
    public static class GenePairRelationTypeImpl implements GenePairRelationType {

        private final String id;
        private final String name = null;
        private final String shortName = null;
        private final IRI uri = null;
        private final boolean decreasingOrderingUsed = false;
        private final List<GenePairRelationType> shownWith = Collections.emptyList();

        @Override
        public GenePairRelationStrengthFormatter getFormatter() {
            return null;
        }

        @Override
        public <T> Comparator<T> newComparator(ToDoubleFunction<T> relationExtractor) {
            return null;
        }

    }

    private File temporaryFile;

    @BeforeMethod
    public void setUp() throws IOException {
        //
        temporaryFile = File.createTempFile("gprdb-test-", ".hdf5");

        //
        try (IHDF5Writer hdfWriter = HDF5Factory.open(this.temporaryFile)) {
            hdfWriter.writeStringArray("/meta/genes", new String[]{"foo", "bar", "baz"});
            hdfWriter.writeStringArray("/meta/genePairRelationTypes", new String[]{"hoge", "piyo"});

            hdfWriter.writeDoubleArray("/genePairRelation/hoge/foo", new double[]{1, 2, 3});
            hdfWriter.writeDoubleArray("/genePairRelation/hoge/bar", new double[]{4, 5, 6});
            hdfWriter.writeDoubleArray("/genePairRelation/hoge/baz", new double[]{7, 8, 9});

            hdfWriter.writeDoubleArray("/genePairRelation/piyo/foo", new double[]{-1, -2, -3});
            hdfWriter.writeDoubleArray("/genePairRelation/piyo/bar", new double[]{-4, -5, -6});
            hdfWriter.writeDoubleArray("/genePairRelation/piyo/baz", new double[]{-7, -8, -9});
        }
    }

    @AfterMethod
    public void tearDown() {
        if (this.temporaryFile.delete()) {
            log.warn("Failed to create a temporary file: {}", this.temporaryFile.getAbsolutePath());
        }
    }

    ///
    /// getGenes
    ///

    @Test
    public void getGenes_returns_correct_result() {
        //
        List<Identifier> genes;
        try (IHDF5Reader hdfReader = HDF5Factory.openForReading(this.temporaryFile)) {
            genes = new GenePairRelationReaderImpl(hdfReader, new IdentifierManagerImpl()).getGenes();
        }

        assertThat(genes).hasSize(3);
        assertThat(genes.get(0).getId()).isEqualTo("foo");
        assertThat(genes.get(1).getId()).isEqualTo("bar");
        assertThat(genes.get(2).getId()).isEqualTo("baz");
    }

    ///
    /// readMap
    ///

    @Test
    public void readMap_returns_correct_data() {
        //
        IdentifierImpl foo = new IdentifierImpl("foo");
        IdentifierImpl bar = new IdentifierImpl("bar");
        IdentifierImpl baz = new IdentifierImpl("baz");

        GenePairRelationTypeImpl hoge = new GenePairRelationTypeImpl("hoge");
        GenePairRelationTypeImpl piyo = new GenePairRelationTypeImpl("piyo");

        try (IHDF5Reader hdfReader = HDF5Factory.openForReading(this.temporaryFile)) {
            //
            GenePairRelationReader reader = new GenePairRelationReaderImpl(hdfReader, new IdentifierManagerImpl());
            assertThat(reader.readMap(hoge, foo))
                    .hasSize(3)
                    .containsEntry(foo, 1.0)
                    .containsEntry(bar, 2.0)
                    .containsEntry(baz, 3.0);
            assertThat(reader.readMap(hoge, bar))
                    .hasSize(3)
                    .containsEntry(foo, 4.0)
                    .containsEntry(bar, 5.0)
                    .containsEntry(baz, 6.0);
            assertThat(reader.readMap(hoge, baz))
                    .hasSize(3)
                    .containsEntry(foo, 7.0)
                    .containsEntry(bar, 8.0)
                    .containsEntry(baz, 9.0);
            assertThat(reader.readMap(piyo, foo))
                    .hasSize(3)
                    .containsEntry(foo, -1.0)
                    .containsEntry(bar, -2.0)
                    .containsEntry(baz, -3.0);
            assertThat(reader.readMap(piyo, bar))
                    .hasSize(3)
                    .containsEntry(foo, -4.0)
                    .containsEntry(bar, -5.0)
                    .containsEntry(baz, -6.0);
            assertThat(reader.readMap(piyo, baz))
                    .hasSize(3)
                    .containsEntry(foo, -7.0)
                    .containsEntry(bar, -8.0)
                    .containsEntry(baz, -9.0);
        }
    }

    @Test
    public void readMap_dont_returns_nan() {
        //
        try (IHDF5Writer hdfWriter = HDF5Factory.open(this.temporaryFile)) {
            hdfWriter.writeDoubleArray("/genePairRelation/hoge/foo", new double[]{1, Double.NaN, 3});
        }

        //
        IdentifierImpl foo = new IdentifierImpl("foo");
        IdentifierImpl baz = new IdentifierImpl("baz");

        GenePairRelationTypeImpl hoge = new GenePairRelationTypeImpl("hoge");

        try (IHDF5Reader hdfReader = HDF5Factory.openForReading(this.temporaryFile)) {
            GenePairRelationReader reader = new GenePairRelationReaderImpl(hdfReader, new IdentifierManagerImpl());
            assertThat(reader.readMap(hoge, foo))
                    .hasSize(2)
                    .containsEntry(foo, 1.0)
                    .containsEntry(baz, 3.0);
        }
    }

    @Test(expectedExceptions = Exception.class)
    public void readMap_throws_an_exception_if_invalid_type_is_specified() {
        //
        GenePairRelationTypeImpl type = new GenePairRelationTypeImpl("foo");
        IdentifierImpl gene = new IdentifierImpl("foo");

        //
        try (IHDF5Reader hdfReader = HDF5Factory.openForReading(this.temporaryFile)) {
            new GenePairRelationReaderImpl(hdfReader, new IdentifierManagerImpl()).readMap(type, gene);
        }
    }

    @Test(expectedExceptions = Exception.class)
    public void readMap_throws_an_exception_if_invalid_gene_is_specified() {
        //
        GenePairRelationTypeImpl type = new GenePairRelationTypeImpl("hoge");
        IdentifierImpl gene = new IdentifierImpl("hoge");

        //
        try (IHDF5Reader hdfReader = HDF5Factory.openForReading(this.temporaryFile)) {
            new GenePairRelationReaderImpl(hdfReader, new IdentifierManagerImpl()).readMap(type, gene);
        }
    }

    ///
    /// close
    ///

    @Test
    public void close_closes_underlying_reader(
            @Mocked IHDF5Reader hdfReader,
            @Mocked IdentifierManager identifierManager) {

        GenePairRelationReaderImpl reader = new GenePairRelationReaderImpl(hdfReader, identifierManager);
        reader.close();

        new Verifications() {{
            hdfReader.close(); times = 1;
        }};
    }

}
