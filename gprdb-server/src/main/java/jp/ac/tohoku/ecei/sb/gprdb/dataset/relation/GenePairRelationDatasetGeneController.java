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
import jp.ac.tohoku.ecei.sb.gprdb.http.controller.AbstractController;
import jp.ac.tohoku.ecei.sb.gprdb.http.controller.PageNotFoundException;
import jp.ac.tohoku.ecei.sb.gprdb.coding.Ref;
import jp.ac.tohoku.ecei.sb.gprdb.http.WebPageUrlProvider;
import lombok.RequiredArgsConstructor;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.impl.SimpleIRI;
import org.openrdf.model.impl.TreeModel;
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
import java.util.List;

/**
 * Request controller to serve {@code /datasets/{dataset}/genes[/page:{page}]}.
 *
 * @author Shu Tadaka
 */
@Controller
@ConditionalOnWebApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GenePairRelationDatasetGeneController extends AbstractController implements WebPageUrlProvider {

    private final GenePairRelationManager genePairRelationManager;

    @RequestMapping(
        value = "/datasets/{dataset}/genes",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE
    )
    public ModelAndView getGenesInDataset(
        @PathVariable("dataset") GenePairRelationDataset dataset) {

        return getGenesInDataset(dataset, 1);
    }

    @RequestMapping(
        value = "/datasets/{dataset}/genes/page:{page}",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE
    )
    public ModelAndView getGenesInDataset(
        @PathVariable("dataset") GenePairRelationDataset dataset,
        @PathVariable("page") int currentPage) {

        //
        Ref<List<Identifier>> genesRef = new Ref<>();
        Ref<Integer> previousPageIndexRef = new Ref<>();
        Ref<Integer> currentPageIndexRef = new Ref<>();
        Ref<Integer> nextPageIndexRef = new Ref<>();

        extractGenesInPage(
            dataset, currentPage, genesRef,
            previousPageIndexRef, currentPageIndexRef, nextPageIndexRef);

        //
        return new ModelAndView("dataset-genes", ImmutableMap.of(
            "dataset", dataset,
            "genes", genesRef.get(),
            "previousPageIndex", previousPageIndexRef.get(),
            "currentPageIndex", currentPageIndexRef.get(),
            "nextPageIndex", nextPageIndexRef.get()
        ));
    }

    @RequestMapping(
        value = "/datasets/{dataset}/genes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public List<Identifier> getGenesInDatasetAsJson(
        @PathVariable("dataset") GenePairRelationDataset dataset) {

        return dataset.getGenes();
    }

    @RequestMapping(
        value = "/datasets/{dataset}/genes/page:{page}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public List<Identifier> getGenesInDatasetAsJson(
        @PathVariable("dataset") GenePairRelationDataset dataset,
        @PathVariable("page") int page) {

        Ref<List<Identifier>> genesRef = new Ref<>();
        extractGenesInPage(dataset, page, genesRef, null, null, null);

        return genesRef.get();
    }

    @RequestMapping(
        value = "/datasets/{dataset}/genes",
        method = RequestMethod.GET,
        produces = {"application/rdf+xml", "text/n3"}
    )
    @ResponseBody
    public Model geneGenesInDatasetAsRdf(@PathVariable("dataset") GenePairRelationDataset dataset) {
        return convertToTriples(dataset, dataset.getGenes());
    }

    @RequestMapping(
        value = "/datasets/{dataset}/genes/page:{page}",
        method = RequestMethod.GET,
        produces = {"application/rdf+xml", "text/n3"}
    )
    @ResponseBody
    public Model geneGenesInDatasetAsRdf(
        @PathVariable("dataset") GenePairRelationDataset dataset,
        @PathVariable("page") int page) {

        //
        Ref<List<Identifier>> genesRef = new Ref<>();
        extractGenesInPage(dataset, page, genesRef, null, null, null);

        //
        return convertToTriples(dataset, genesRef.get());
    }

    private void extractGenesInPage(
        @NotNull GenePairRelationDataset dataset,
        @Min(1) int currentPage,
        @NotNull Ref<List<Identifier>> genesRef,
        Ref<Integer> previousPageIndexRef,
        Ref<Integer> currentPageIndexRef,
        Ref<Integer> nextPageIndexRef) {

        //
        List<Identifier> genes = dataset.getGenes();

        int numGenes = genes.size();
        int numGenesInPage = 100;
        int numPages = numGenes / numGenesInPage + (numGenes % numGenesInPage == 0 ? 0 : 1);

        int currentPageIndex = currentPage - 1;
        if (currentPageIndex < 0 || numPages <= currentPageIndex) {
            throw new PageNotFoundException();
        }

        int previousPageIndex = currentPageIndex - 1;
        int nextPageIndex = currentPageIndex < numPages - 1 ? currentPageIndex + 1 : -1;

        int geneStartIndex = currentPageIndex * numGenesInPage;
        int geneEndIndex = Math.min(geneStartIndex + numGenesInPage, numGenes);

        //
        genesRef.set(genes.subList(geneStartIndex, geneEndIndex));

        if (previousPageIndexRef != null && currentPageIndexRef != null && nextPageIndexRef != null) {
            previousPageIndexRef.set(previousPageIndex);
            currentPageIndexRef.set(currentPageIndex);
            nextPageIndexRef.set(nextPageIndex);
        }
    }

    private static Model convertToTriples(
        @NotNull GenePairRelationDataset dataset, @NotNull List<Identifier> genes) {

        Model model = new TreeModel();
        IRI datasetUri = new SimpleIRI("http://localhost:8080/datasets/" + dataset.getId());
        IRI hasGeneUri = new SimpleIRI("http://rdf.gprdb.org/vocabrary/has-gene");

        for (Identifier gene : dataset.getGenes()) {
            model.add(datasetUri, hasGeneUri, new SimpleIRI("http://identifiers.org/ncbigene:" + gene.getBody()));
        }

        return model;
    }

    @Override
    public Iterable<String> getCacheablePages() {
        return new GenesInDatasetPageUrlGenerator();
    }

    private class GenesInDatasetPageUrlGenerator extends Generator<String> {

        @Override
        protected void run() {
            GenePairRelationManager manager = GenePairRelationDatasetGeneController.this.genePairRelationManager;
            for (GenePairRelationDataset dataset : manager.getDatasets()) {
                //
                yield("/datasets/" + dataset.getId() + "/genes");

                //
                int numGenes = dataset.getNumGenes();
                int numGenesInPage = 100;
                int numPages = numGenes / numGenesInPage + (numGenes % numGenesInPage == 0 ? 0 : 1);
                for (int i = 1; i <= numPages; i++) {
                    yield("/datasets/" + dataset.getId() + "/genes/page:" + i);
                }
            }
        }

    }

}
