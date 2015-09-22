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

import com.google.common.collect.ImmutableMap;
import com.zoominfo.util.yieldreturn.Generator;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.http.WebPageUrlProvider;
import jp.ac.tohoku.ecei.sb.gprdb.http.controller.AbstractController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Request controller to serve {@code /datasets/{guideDataset}/{guideGene}/{relationType}/table[/upto:{count}]}.
 *
 * @author Shu Tadaka
 */
@Controller
@ConditionalOnWebApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GenePairRelationDatasetTableController extends AbstractController implements WebPageUrlProvider {

    private final ListableBeanFactory beanFactory;
    private final GenePairRelationManager genePairRelationManager;

    @RequestMapping(
        value = "/datasets/{guideDataset}/{guideGene}/{relationType}/table",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE
    )
    public ModelAndView getRelationTable(
        @PathVariable("guideDataset") GenePairRelationDataset dataset,
        @PathVariable("guideGene") Identifier guideGene,
        @PathVariable("relationType") GenePairRelationType relationType) {

        return getRelationTable(dataset, guideGene, relationType, 100);
    }

    @RequestMapping(
        value = "/datasets/{guideDataset}/{guideGene}/{relationType}/table/upto:{count}",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE
    )
    public ModelAndView getRelationTable(
        @PathVariable("guideDataset") GenePairRelationDataset dataset,
        @PathVariable("guideGene") Identifier guideGene,
        @PathVariable("relationType") GenePairRelationType relationType,
        @PathVariable("count") int count) {

        return new ModelAndView("dataset-relationTable", ImmutableMap.<String, Object>builder()
            .put("guideDataset", dataset)
            .put("guideGene", guideGene)
            .put("relationType", relationType)
            .put("table", createRelationTable(dataset, guideGene, relationType, count))
            .put("upTo", count)
            .build());
    }

    @RequestMapping(
        value = "/datasets/{guideDataset}/{guideGene}/{relationType}/table/upto:{count}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public GenePairRelationTable getRelationTableAsJson(
        @PathVariable("guideDataset") GenePairRelationDataset dataset,
        @PathVariable("guideGene") Identifier guideGene,
        @PathVariable("relationType") GenePairRelationType relationType,
        @PathVariable("count") int count) {

        return createRelationTable(dataset, guideGene, relationType, count);
    }

    private GenePairRelationTable createRelationTable(
        @NotNull GenePairRelationDataset dataset,
        @NotNull Identifier guideGene,
        @NotNull GenePairRelationType relationType,
        @Min(1) int count) {

        return this.genePairRelationManager.createTable(dataset, guideGene, relationType, 0, count);
    }

    @Override
    public Iterable<String> getCacheablePages() {
        return new RelationTablePageUrlGenerator();
    }

    private class RelationTablePageUrlGenerator extends Generator<String> {

        private final int[] NUM_GENES_SETS = new int[]{100, 200, 300};

        @Override
        protected void run() {
            GenePairRelationManager manager = GenePairRelationDatasetTableController.this.genePairRelationManager;
            for (GenePairRelationDataset dataset : manager.getDatasets()) {
                for (Identifier guideGene : dataset.getGenes()) {
                    for (GenePairRelationType relationType : dataset.getRelationTypes()) {
                        generateUrls(dataset, guideGene, relationType);
                    }
                }
            }
        }

        private void generateUrls(
            @NotNull GenePairRelationDataset dataset,
            @NotNull Identifier guideGene,
            @NotNull GenePairRelationType relationType) {

            //
            String prefix = "/datasets/" + dataset.getId() +
                "/" + guideGene.getId() +
                "/" + relationType.getId() + "/table";

            //
            yield(prefix);
            for (int numGenes : NUM_GENES_SETS) {
                yield(prefix + "/upto:" + numGenes);
            }
        }

    }

}
