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

import com.google.common.collect.ImmutableList;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class GenePairRelationDatasetImplTest {

    ///
    /// setup
    ///

    @Mocked
    private IdentifierManager identifierManager;

    @Mocked
    private GenePairRelationManager genePairRelationManager;

    @Mocked
    private GenePairRelationConfig.DatasetConfig datasetConfig;

    private GenePairRelationConfig.RdfVocabularyConfig rdfVocabularyConfig;

    private GenePairRelationDatasetImpl dataset;

    @BeforeMethod
    public void setUp() {
        dataset = new GenePairRelationDatasetImpl(
            identifierManager, genePairRelationManager, datasetConfig, rdfVocabularyConfig);
    }

    ///
    /// getVersion
    ///

    @Test
    public void getVersion_returns_correct_result() {
        new NonStrictExpectations() {{
            datasetConfig.getVersion(); result = "foo-bar-baz";
        }};

        assertThat(dataset.getVersion()).isEqualTo("foo-bar-baz");

        new Verifications() {{
            datasetConfig.getVersion(); times = 1;
        }};
    }

    ///
    /// getRelationTypes
    ///

    @Test
    public void getRelationTypes_returns_correct_result(
        @Mocked GenePairRelationType type1, @Mocked GenePairRelationType type2) {

        new NonStrictExpectations() {{
            datasetConfig.getRelationTypes(); result = ImmutableList.of("foo", "bar");
            genePairRelationManager.getTypes(); result = ImmutableList.of(type1, type2);
            type1.getId(); result = "foo";
            type2.getId(); result = "bar";
        }};

        assertThat(dataset.getRelationTypes()).hasSize(2);
        assertThat(dataset.getRelationTypes().get(0).getId()).isEqualTo("foo");
        assertThat(dataset.getRelationTypes().get(1).getId()).isEqualTo("bar");
    }

    ///
    /// getSpecies
    ///

    @Test
    public void getSpecies_returns_correct_result(@Mocked Identifier speciesMock) {
        new NonStrictExpectations() {{
            identifierManager.wrap("foo:bar"); result = speciesMock;
            datasetConfig.getSpecies(); result = "foo:bar";
        }};

        assertThat(dataset.getSpecies()).isNotNull().isSameAs(speciesMock);

        new Verifications() {{
            identifierManager.wrap("foo:bar"); times = 1;
            datasetConfig.getSpecies(); times = 1;
        }};
    }

    ///
    /// getGenes
    ///

    @Test
    public void getGenes_returns_correct_result(
            @Mocked GenePairRelationAccessorFactory accessorFactory,
            @Mocked GenePairRelationReader reader,
            @Mocked List<Identifier> genes) {

        new NonStrictExpectations() {{
            genePairRelationManager.getAccessorFactory(); result = accessorFactory;
            accessorFactory.newReader((String)any); result = reader;
            reader.getGenes(); result = genes;
        }};

        assertThat(dataset.getGenes()).isSameAs(genes);

        new Verifications() {{
            genePairRelationManager.getAccessorFactory(); times = 1;
            accessorFactory.newReader(dataset.getId()); times = 1;
            reader.getGenes(); times = 1;
        }};
    }

    @Test
    public void getGenes_returns_empty_if_failed_to_create_reader(
            @Mocked GenePairRelationAccessorFactory accessorFactory) {

        new NonStrictExpectations() {{
            genePairRelationManager.getAccessorFactory(); result = accessorFactory;
            accessorFactory.newReader((String)any); result = new RuntimeException("ERROR");
        }};

        assertThat(dataset.getGenes()).isEmpty();
    }

    ///
    /// getNumGenes
    ///

    @Test
    public void getNumGenes_returns_correct_result(
            @Mocked GenePairRelationAccessorFactory accessorFactory,
            @Mocked GenePairRelationReader reader,
            @Mocked List<Identifier> genes) {

        new NonStrictExpectations() {{
            genePairRelationManager.getAccessorFactory(); result = accessorFactory;
            accessorFactory.newReader((String)any); result = reader;
            reader.getGenes(); result = genes;
            genes.size(); result = 1234;
        }};

        assertThat(dataset.getNumGenes()).isEqualTo(1234);

        new Verifications() {{
            genePairRelationManager.getAccessorFactory(); times = 1;
            accessorFactory.newReader(dataset.getId()); times = 1;
            reader.getGenes(); times = 1;
            genes.size(); times = 1;
        }};
    }

    ///
    /// newReader
    ///

    @Test
    public void newReader_returns_correct_result(
            @Mocked GenePairRelationAccessorFactory accessorFactory,
            @Mocked GenePairRelationReader reader)  {

        new NonStrictExpectations() {{
            genePairRelationManager.getAccessorFactory(); result = accessorFactory;
            accessorFactory.newReader((String)any); result = reader;
        }};

        assertThat(dataset.newReader()).isSameAs(reader);

        new Verifications() {{
            genePairRelationManager.getAccessorFactory(); times = 1;
            accessorFactory.newReader(dataset.getId()); times = 1;
        }};
    }

    ///
    /// getLoaderFactoryClass
    ///

    @Test
    public void getLoaderFactoryClass_returns_correct_result() {
        new NonStrictExpectations() {{
            datasetConfig.getLoaderFactoryClass(); result = "foo.bar.baz";
        }};

        assertThat(dataset.getLoaderFactoryClass()).isEqualTo("foo.bar.baz");

        new Verifications() {{
            datasetConfig.getLoaderFactoryClass(); times = 1;
        }};
    }

    ///
    /// getLoaderConfig
    ///

    @Test
    public void getLoaderConfig_returns_correct_result(@Mocked Map<String, Object> loaderConfig) {
        new NonStrictExpectations() {{
            datasetConfig.getLoaderConfig(); result = loaderConfig;
        }};

        assertThat(dataset.getLoaderConfig()).isSameAs(loaderConfig);

        new Verifications() {{
            datasetConfig.getLoaderConfig(); times = 1;
        }};
    }

}
