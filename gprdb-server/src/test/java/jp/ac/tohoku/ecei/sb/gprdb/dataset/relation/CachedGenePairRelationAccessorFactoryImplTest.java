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

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
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
public class CachedGenePairRelationAccessorFactoryImplTest {

    ///
    /// setup
    ///

    private static final String DATASET1 = "DATASET1";

    private static final String DATASET2 = "DATASET2";

    @Mocked
    private GenePairRelationAccessorFactory parent;

    @Mocked
    private GenePairRelationReader reader11;

    @Mocked
    private GenePairRelationReader reader12;

    @Mocked
    private GenePairRelationReader reader2;

    @Mocked
    private GenePairRelationWriter writer11;

    @Mocked
    private GenePairRelationWriter writer12;

    @Mocked
    private GenePairRelationWriter writer2;

    private CachedGenePairRelationAccessorFactoryImpl accessorFactory;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() {
        //
        new NonStrictExpectations() {{
            parent.newReader(DATASET1);
            result = reader11;
            result = reader12;

            parent.newReader(DATASET1);
            result = reader2;

            parent.newWriter(DATASET1, (List<GenePairRelationType>)any, (List<Identifier>)any);
            result = writer11;
            result = writer12;

            parent.newWriter(DATASET2, (List<GenePairRelationType>)any, (List<Identifier>)any);
            result = writer2;
        }};

        //
        this.accessorFactory = new CachedGenePairRelationAccessorFactoryImpl(this.parent, 3);
    }

    ///
    /// newReader
    ///

    @Test
    public void newReader_returns_cached_instance() {
        GenePairRelationReader reader11 = this.accessorFactory.newReader(DATASET1);
        GenePairRelationReader reader12 = this.accessorFactory.newReader(DATASET1);
        GenePairRelationReader reader21 = this.accessorFactory.newReader(DATASET2);

        assertThat(reader11).isSameAs(reader12).isNotSameAs(reader21);
        assertThat(reader12).isSameAs(reader11).isNotSameAs(reader21);

        new Verifications() {{
            parent.newReader(DATASET1); times = 1;
            parent.newReader(DATASET2); times = 1;
        }};
    }

    ///
    /// clearReaderCache
    ///

    @Test
    public void clearReaderCache_cleans_cache() throws InterruptedException {
        GenePairRelationReader reader11 = this.accessorFactory.newReader(DATASET1);

        Thread.sleep(5000);
        this.accessorFactory.cleanReaderCache();

        GenePairRelationReader reader12 = this.accessorFactory.newReader(DATASET1);
        assertThat(reader11).isNotSameAs(reader12);

        new Verifications() {{
            parent.newReader(DATASET1); times = 2;
        }};
    }

    ///
    /// newWriter
    ///

    @Test
    public void newWriter_returns_correct_result(
            @Mocked List<GenePairRelationType> types, @Mocked List<Identifier> genes) {

        GenePairRelationWriter writer11 = this.accessorFactory.newWriter(DATASET1, types, genes);
        GenePairRelationWriter writer12 = this.accessorFactory.newWriter(DATASET1, types, genes);
        GenePairRelationWriter writer21 = this.accessorFactory.newWriter(DATASET2, types, genes);

        assertThat(writer11).isNotSameAs(writer12).isNotSameAs(writer21);
        assertThat(writer12).isNotSameAs(writer11).isNotSameAs(writer21);
        assertThat(writer21).isNotSameAs(writer11).isNotSameAs(writer12);

        new Verifications() {{
            parent.newWriter(DATASET1, types, genes); times = 2;
            parent.newWriter(DATASET2, types, genes); times = 1;
        }};
    }

}
