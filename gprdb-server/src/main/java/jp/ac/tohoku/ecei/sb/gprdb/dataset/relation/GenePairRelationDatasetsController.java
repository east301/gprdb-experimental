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
import jp.ac.tohoku.ecei.sb.gprdb.http.controller.AbstractController;
import lombok.RequiredArgsConstructor;
import org.openrdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Request controller to handle requests to "/datasets".
 *
 * @author Shu Tadaka
 */
@Controller
@ConditionalOnWebApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GenePairRelationDatasetsController extends AbstractController {

    private final GenePairRelationManager genePairRelationManager;
    private final GenePairRelationExporter genePairRelationExporter;

    @RequestMapping(value = "/datasets", method = RequestMethod.GET, produces = HTML)
    public ModelAndView getDatasets() {
        return new ModelAndView("datasets", ImmutableMap.of(
            "datasets", this.genePairRelationManager.getDatasets())
        );
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.GET, produces = JSON)
    @ResponseBody
    public List<GenePairRelationDatasetJsonV1> getDatasetsAsJson() {
        return this.genePairRelationExporter.getDatasetsAsJsonV1();
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.GET, produces = {RDFXML, N3})
    @ResponseBody
    public Model getDatasetsAsRdf() {
        return this.genePairRelationExporter.getDatasetsAsRdfV1();
    }

    @RequestMapping(value = "/datasets/{dataset}", method = RequestMethod.GET, produces = JSON)
    @ResponseBody
    public GenePairRelationDatasetJsonV1 getDatasetAsJson(
        @PathVariable GenePairRelationDataset dataset) {

        return this.genePairRelationExporter.getDatasetAsJsonV1(dataset);
    }

    @RequestMapping(value = "/datasets/{dataset}", method = RequestMethod.GET, produces = {RDFXML, N3})
    @ResponseBody
    public Model getDatasetAsRdf(@PathVariable GenePairRelationDataset dataset) {
        return this.genePairRelationExporter.getDatasetAsRdfV1(dataset);
    }

}
