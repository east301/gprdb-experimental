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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class GenePairRelationReaderCacheWrapperTest {

    ///
    /// setup
    ///

    @Mocked
    private GenePairRelationReader parent;

    @Mocked
    private List<Identifier> genes;

    @Mocked
    private Map<Identifier, Double> relationMap;

    private GenePairRelationReaderCacheWrapper wrapper;

    @BeforeMethod
    public void setUp() {
        //
        new NonStrictExpectations() {{
            parent.getGenes(); result = genes;
            parent.readMap((GenePairRelationType)any, (Identifier)any); result = relationMap;
        }};

        this.wrapper = new GenePairRelationReaderCacheWrapper(this.parent);
    }

    ///
    /// getGenes
    ///

    @Test
    public void getGenes_returns_correct_result() {
        assertThat(this.wrapper.getGenes()).isSameAs(this.genes);

        new Verifications() {{
            parent.getGenes(); times = 1;
        }};
    }

    ///
    /// readMap
    ///

    @Test
    public void readMap_returns_correct_result(@Mocked GenePairRelationType type, @Mocked Identifier guideGene) {
        assertThat(this.wrapper.readMap(type, guideGene)).isSameAs(this.relationMap);

        new Verifications() {{
            parent.readMap(type, guideGene); times = 1;
        }};
    }

    ///
    /// close
    ///

    @Test
    public void close_do_nothing() {
        this.wrapper.close();

        new Verifications() {{
            parent.close(); times = 0;
        }};
    }

    ///
    /// dispose
    ///

    @Test
    public void dispose_closes_underlying_reader() {
        this.wrapper.dispose();

        new Verifications() {{
            parent.close(); times = 1;
        }};
    }

}
