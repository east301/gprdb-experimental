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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierMappingType;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierMappingTypeFactory;
import jp.ac.tohoku.ecei.sb.gprdb.coding.Pair;
import jp.ac.tohoku.ecei.sb.gprdb.coding.Ref;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Shu Tadaka
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GenePairRelationTableFactory {

    private final ListableBeanFactory beanFactory;
    private final IdentifierMappingTypeFactory identifierMappingTypeFactory;

    @NotNull
    public GenePairRelationTable create(
        @NotNull List<GenePairRelationDataset> datasets,
        @NotNull GenePairRelationDataset guideDataset,
        @NotNull GenePairRelationType guideRelationType,
        @NotNull Identifier guideGene,
        @Min(0) int start,
        @Min(1) int count) {

        //
        List<GenePairRelationTableColumn> columns = Lists.newArrayList();

        //
        Ref<List<Identifier>> guideInteractingGenesRef = new Ref<>();
        Ref<GenePairRelationTableColumn> guideColumnRef = new Ref<>();
        createGuideColumn(
            guideDataset, guideRelationType, guideGene, start, count, guideInteractingGenesRef, guideColumnRef);

        columns.add(guideColumnRef.get());

        //
        for (Pair<GenePairRelationDataset, GenePairRelationType> pair
            : getCandidateColumns(datasets, guideDataset, guideRelationType)) {

            GenePairRelationDataset dataset = pair.getFirst();
            GenePairRelationType relationType = pair.getSecond();

            List<GenePairRelationTableColumn> subColumns = dataset.getSpecies().equals(guideDataset.getSpecies())
                ? tryToCreateSameSpeciesColumns(guideGene, guideInteractingGenesRef.get(), dataset, relationType)
                : tryToCreateDifferentSpeciesColumns(guideGene, guideInteractingGenesRef.get(), dataset, relationType);

            columns.addAll(subColumns);
        }

        //
        GenePairRelationTableImpl table = new GenePairRelationTableImpl(
            guideDataset, guideGene, guideInteractingGenesRef.get(), columns);

        List<GenePairRelationTableCustomColumnInjector> customColumns = Lists.newArrayList(
            this.beanFactory.getBeansOfType(GenePairRelationTableCustomColumnInjector.class).values());
        customColumns.forEach(c -> c.prepare(table));

        table.setCustomColumns(customColumns);

        //
        return table;
    }

    private static void createGuideColumn(
        @NotNull GenePairRelationDataset guideDataset,
        @NotNull GenePairRelationType guideRelationType,
        @NotNull Identifier guideGene,
        @Min(0) int start,
        @Min(1) int count,
        @NotNull Ref<List<Identifier>> guideInteractingGenesRef,
        @NotNull Ref<GenePairRelationTableColumn> guideColumnRef) {

        //
        Map<Identifier, Double> relationMap;
        try (GenePairRelationReader reader = guideDataset.newReader()) {
            relationMap = reader.readMap(guideRelationType, guideGene);
        }

        List<Map.Entry<Identifier, Double>> orderedRelations = relationMap
            .entrySet()
            .stream()
            .filter(e -> !e.getKey().equals(guideGene))
            .sorted(guideRelationType.newComparator(Map.Entry::getValue))
            .skip(start)
            .limit(count)
            .collect(Collectors.toList());

        //
        guideInteractingGenesRef.set(
            orderedRelations.stream().map(Map.Entry::getKey).collect(Collectors.toList()));

        //
        Multimap<Identifier, GenePairRelationMapping> relationMultimap = ArrayListMultimap.create();
        orderedRelations.forEach(
            e -> relationMultimap.put(e.getKey(), new GenePairRelationMappingImpl(e.getKey(), e.getValue())));

        guideColumnRef.set(
            new GenePairRelationTableColumnImpl(guideDataset, guideGene, guideRelationType, relationMultimap));
    }

    private static List<Pair<GenePairRelationDataset, GenePairRelationType>> getCandidateColumns(
        @NotNull List<GenePairRelationDataset> datasets,
        @NotNull GenePairRelationDataset guideDataset,
        @NotNull GenePairRelationType guideRelationType) {

        List<Pair<GenePairRelationDataset, GenePairRelationType>> result = Lists.newArrayList();

        for (GenePairRelationDataset dataset : datasets) {
            //
            if (guideDataset.equals(dataset)) {
                continue;
            }

            //
            Collection<GenePairRelationType> relationTypes = dataset.getRelationTypes().contains(guideRelationType)
                ? ImmutableList.of(guideRelationType)
                : Collections2.filter(dataset.getRelationTypes(), t -> t.getShownWith().contains(guideRelationType));

            for (GenePairRelationType relationType : relationTypes) {
                result.add(new Pair<>(dataset, relationType));
            }
        }

        return result;
    }

    private List<GenePairRelationTableColumn> tryToCreateSameSpeciesColumns(
        @NotNull Identifier guideGene,
        @NotNull Collection<Identifier> guideInteractingGenes,
        @NotNull GenePairRelationDataset dataset,
        @NotNull GenePairRelationType relationType) {

        //
        Multimap<Identifier, Identifier> geneMap = ArrayListMultimap.create();
        if (dataset.getGenes().contains(guideGene)) {
            geneMap.put(guideGene, guideGene);
            for (Identifier interactingGene : guideInteractingGenes) {
                geneMap.put(interactingGene, interactingGene);
            }
        } else {
            IdentifierMappingType mappingType
                = this.identifierMappingTypeFactory.newIntraSpeciesIdentifierMappingType();

            geneMap.putAll(guideGene, guideGene.getMappingResults(mappingType));
            for (Identifier interactingGene : guideInteractingGenes) {
                geneMap.putAll(interactingGene, interactingGene.getMappingResults(mappingType));
            }
        }

        return tryToCreateSameSpeciesColumns(guideGene, guideInteractingGenes, dataset, relationType, geneMap);
    }

    private List<GenePairRelationTableColumn> tryToCreateSameSpeciesColumns(
        @NotNull Identifier guideGene,
        @NotNull Collection<Identifier> guideInteractingGenes,
        @NotNull GenePairRelationDataset dataset,
        @NotNull GenePairRelationType relationType,
        @NotNull Multimap<Identifier, Identifier> geneMap) {

        //
        List<GenePairRelationTableColumn> columns = Lists.newArrayList();

        for (Identifier mappedGuideGene : geneMap.get(guideGene)) {
            //
            Multimap<Identifier, GenePairRelationMapping> result = ArrayListMultimap.create();
            try (GenePairRelationReader reader = dataset.newReader()) {
                //
                if (!reader.containsGene(mappedGuideGene)) {
                    continue;
                }

                //
                Map<Identifier, Double> relationMap = reader.readMap(relationType, mappedGuideGene);

                for (Identifier interactingGene : guideInteractingGenes) {
                    for (Identifier mappedInteractingGene : geneMap.get(interactingGene)) {
                        Double strength = relationMap.get(mappedInteractingGene);
                        if (strength != null) {
                            result.put(
                                interactingGene,
                                new GenePairRelationMappingImpl(mappedInteractingGene, strength));
                        }
                    }
                }
            }

            //
            columns.add(new GenePairRelationTableColumnImpl(dataset, mappedGuideGene, relationType, result));
        }

        //
        return columns;
    }

    private List<GenePairRelationTableColumn> tryToCreateDifferentSpeciesColumns(
        @NotNull Identifier guideGene,
        @NotNull Collection<Identifier> guideInteractingGenes,
        @NotNull GenePairRelationDataset dataset,
        @NotNull GenePairRelationType relationType) {

        //
        Multimap<Identifier, Identifier> geneMap = mapGenesToDifferentSpecies(
            guideGene, guideInteractingGenes, dataset.getSpecies());

        if (!geneMap.containsKey(guideGene)) {
            return Collections.emptyList();
        }

        //
        List<GenePairRelationTableColumn> columns = Lists.newArrayList();

        try (GenePairRelationReader reader = dataset.newReader()) {
            for (Identifier mappedGuideGene : geneMap.get(guideGene)) {
                if (!reader.containsGene(mappedGuideGene)) {
                    continue;
                }
                //
                Map<Identifier, Double> originalRelationMap = reader.readMap(relationType, mappedGuideGene);
                Multimap<Identifier, GenePairRelationMapping> mappedRelationMap
                    = mapRelationMapToDifferentSpecies(guideInteractingGenes, originalRelationMap, geneMap);

                //
                columns.add(new GenePairRelationTableColumnImpl(
                    dataset, mappedGuideGene, relationType, mappedRelationMap));
            }
        }

        return columns;
    }

    private Multimap<Identifier, Identifier> mapGenesToDifferentSpecies(
        @NotNull Identifier guideGene,
        @NotNull Collection<Identifier> guideInteractingGenes,
        @NotNull Identifier destinationSpecies) {

        //
        IdentifierMappingType mappingType = this.identifierMappingTypeFactory
            .newInterSpeciesIdentifierMappingType(destinationSpecies);

        //
        List<Identifier> mappedGuideGenes = guideGene.getMappingResults(mappingType);
        if (mappedGuideGenes.isEmpty()) {
            return ImmutableMultimap.of();
        }

        //
        Multimap<Identifier, Identifier> result = HashMultimap.create();
        result.putAll(guideGene, mappedGuideGenes);

        for (Identifier guideInteractingGene : guideInteractingGenes) {
            result.putAll(guideInteractingGene, guideInteractingGene.getMappingResults(mappingType));
        }

        return result;
    }

    private static Multimap<Identifier, GenePairRelationMapping> mapRelationMapToDifferentSpecies(
        @NotNull Collection<Identifier> guideInteractingGenes,
        @NotNull Map<Identifier, Double> originalRelationMap,
        @NotNull Multimap<Identifier, Identifier> geneMap) {

        Multimap<Identifier, GenePairRelationMapping> result = HashMultimap.create();

        for (Identifier guideInteractingGene : guideInteractingGenes) {
            for (Identifier mappedInteractingGenes : geneMap.get(guideInteractingGene)) {
                Double strength = originalRelationMap.get(mappedInteractingGenes);
                if (strength != null) {
                    result.put(guideInteractingGene, new GenePairRelationMappingImpl(mappedInteractingGenes, strength));
                }
            }
        }

        return result;
    }


}
