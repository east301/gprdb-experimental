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

import com.google.common.collect.Lists;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import lombok.RequiredArgsConstructor;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.impl.NumericLiteral;
import org.openrdf.model.impl.SimpleIRI;
import org.openrdf.model.impl.SimpleLiteral;
import org.openrdf.model.impl.TreeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Shu Tadaka
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GenePairRelationExporterImpl implements GenePairRelationExporter {

    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";

    private static final IRI DATABASE_CONTAINS_DATASET_PREFICATE
        = new SimpleIRI("http://purl.org/vocab/resourcelist/schema#contains");

    private final GenePairRelationManager manager;
    private final GenePairRelationConfig config;

    ///
    /// a set of datasets
    ///

    @Override
    public List<GenePairRelationDatasetJsonV1> getDatasetsAsJsonV1() {
        return Lists.transform(
            this.manager.getDatasets(),
            GenePairRelationDatasetJsonV1Impl::new);
    }

    @Override
    public Model getDatasetsAsRdfV1() {
        //
        IRI rootDatabase = new SimpleIRI("http://coxpresdb.jp");
        Model model = new TreeModel();

        for (GenePairRelationDataset dataset : this.manager.getDatasets()) {
            model.add(rootDatabase, DATABASE_CONTAINS_DATASET_PREFICATE, dataset.getUri());
            model.addAll(getDatasetAsRdfV1(dataset));
        }

        //
        return model;
    }

    ///
    /// single dataset
    ///

    @Override
    public GenePairRelationDatasetJsonV1 getDatasetAsJsonV1(
        @NotNull GenePairRelationDataset dataset) {

        return new GenePairRelationDatasetJsonV1Impl(dataset);
    }

    @Override
    public Model getDatasetAsRdfV1(@NotNull GenePairRelationDataset dataset) {
        //
        GenePairRelationConfig.RdfVocabularyConfig vocab = this.config.getRdfVocabularies();
        IRI datasetUri = dataset.getUri();
        Model model = new TreeModel();

        //
        model.add(
            datasetUri,
            new SimpleIRI(RDF + "type"),
            new SimpleIRI(vocab.getDatasetTypeObject()));
        model.add(
            datasetUri,
            new SimpleIRI(RDFS + "label"),
            new SimpleLiteral(dataset.getName()));
        model.add(
            datasetUri,
            new SimpleIRI(vocab.getDatasetVersionPredicate()),
            new SimpleLiteral(dataset.getVersion()));

        Optional<IRI> speciesUri = dataset.getSpecies().getUri();
        if (speciesUri.isPresent()) {
            model.add(datasetUri, new SimpleIRI(vocab.getDatasetSpeciesPredicate()), speciesUri.get());
        }

        //
        return model;
    }

    @Override
    public Model getRelationsAsRdfV1(
        @NotNull GenePairRelationDataset dataset,
        @NotNull Identifier guideGene,
        @NotNull GenePairRelationType relationType) {

        return getRelationsAsRdfV1(
            dataset, guideGene, relationType, map -> filterTop(relationType, map, 100));
    }

    private static Collection<Identifier> filterTop(
        @NotNull GenePairRelationType relationType,
        @NotNull Map<Identifier, Double> relationMap,
        @Min(0) int threshold) {

        return relationMap
            .entrySet()
            .stream()
            .sorted(relationType.newComparator(Map.Entry::getValue))
            .limit(threshold)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    @Override
    public Model getRelationsAsRdfV1(
        @NotNull GenePairRelationDataset dataset,
        @NotNull Identifier guideGene,
        @NotNull GenePairRelationType relationType,
        @NotNull Function<Map<Identifier, Double>, Collection<Identifier>> filter) {

        Model model = new TreeModel();

        try (GenePairRelationReader reader = dataset.newReader()) {
            Map<Identifier, Double> relationMap = reader.readMap(relationType, guideGene);
            for (Identifier interactingGene : filter.apply(relationMap)) {
                model.add(
                    new SimpleIRI("http://example.com/" + guideGene.getId() + "/" + interactingGene.getId()),
                    relationType.getUri(),
                    new NumericLiteral(relationMap.get(interactingGene)));
            }
        }

        return model;
    }

    @Override
    public Model getRelationsAsRdfV1(
        @NotNull GenePairRelationDataset dataset,
        @NotNull Identifier gene1,
        @NotNull Identifier gene2) {

        Model model = new TreeModel();

        try (GenePairRelationReader reader = dataset.newReader()) {
            for (GenePairRelationType relationType : dataset.getRelationTypes()) {
                Map<Identifier, Double> relationMap = reader.readMap(relationType, gene1);
                if (relationMap.containsKey(gene2)) {
                    model.add(
                        new SimpleIRI("http://example.com/" + gene1.getId() + "/" + gene2.getId()),
                        relationType.getUri(),
                        new NumericLiteral(relationMap.get(gene2)));
                }
            }
        }

        return model;
    }

}
