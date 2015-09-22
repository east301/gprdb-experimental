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

package jp.ac.tohoku.ecei.sb.gprdb.dataset.id;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStore;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStoreManager;
import lombok.Getter;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Internal data shared among identifier-related objects.
 *
 * @author Shu Tadaka
 */
@Component
@Getter
class IdentifierInternalSharedData {

    private final IdentifierConfig rootConfig;
    private final IdentifierMappingTypeFactory identifierMappingTypeFactory;
    private final IdentifierInfoEntityRepository identifierInfoEntityRepository;
    private final IdentifierMappingEntityRepository identifierMappingEntityRepository;
    private final IdentifierEntityFullTextSearchService identifierEntityFullTextSearchService;

    private final Map<String, IdentifierDataset> identifierDatasetMap;
    private final ListMultimap<String, IdentifierUriResolver> identifierUriResolverMap;
    private final ListMultimap<String, IdentifierLinkResolver> identifierLinkGeneratorMap;
    private final KeyValueStore<String, String> identifierNameCache;
    private final KeyValueStore<String, String> identifierDescriptionCache;
    private final KeyValueStore<String, String> identifierIntraSpeciesMappingCache;
    private final KeyValueStore<String, String> identifierInterSpeciesMappingCache;

    @Autowired
    public IdentifierInternalSharedData(
        @NotNull ListableBeanFactory beanFactory,
        @NotNull IdentifierConfig rootConfig,
        @NotNull IdentifierMappingTypeFactory identifierMappingTypeFactory,
        @NotNull IdentifierInfoEntityRepository identifierInfoEntityRepository,
        @NotNull IdentifierMappingEntityRepository identifierMappingEntityRepository,
        @NotNull IdentifierEntityFullTextSearchService identifierEntityFullTextSearchService,
        @NotNull KeyValueStoreManager keyValueStoreManager) {

        this.rootConfig = rootConfig;
        this.identifierMappingTypeFactory = identifierMappingTypeFactory;
        this.identifierInfoEntityRepository = identifierInfoEntityRepository;
        this.identifierMappingEntityRepository = identifierMappingEntityRepository;
        this.identifierEntityFullTextSearchService = identifierEntityFullTextSearchService;

        this.identifierDatasetMap = createIdentifierDatasetMap(rootConfig);
        this.identifierUriResolverMap = createIdentifierUriResolverMap(beanFactory, rootConfig);
        this.identifierLinkGeneratorMap = createIdentifierLinkGeneratorMap(beanFactory, rootConfig);
        this.identifierNameCache = keyValueStoreManager.getMap("identifier-name-cache");
        this.identifierDescriptionCache = keyValueStoreManager.getMap("identifier-description-cache");
        this.identifierIntraSpeciesMappingCache = keyValueStoreManager.getMap("identifier-intraspecies-mapping-cache");
        this.identifierInterSpeciesMappingCache = keyValueStoreManager.getMap("identifier-interspecies-mapping-cache");
    }

    private static ListMultimap<String, IdentifierUriResolver> createIdentifierUriResolverMap(
        @NotNull ListableBeanFactory beanFactory, @NotNull IdentifierConfig rootConfig) {

        //
        Map<String, IdentifierUriResolver> resolvers = beanFactory
            .getBeansOfType(IdentifierUriResolver.class)
            .values()
            .stream()
            .collect(Collectors.toMap(g -> g.getClass().getName(), Function.identity()));

        //
        ListMultimap<String, IdentifierUriResolver> result = ArrayListMultimap.create();
        for (String className : rootConfig.getUriResolvers()) {
            IdentifierUriResolver resolver = resolvers.get(className);
            if (resolver == null) {
                throw new RuntimeException("Not found: " + className);
            }

            for (String prefix : resolver.getSupportedIdentifierPrefixes()) {
                result.put(prefix, resolver);
            }
        }

        return ImmutableListMultimap.copyOf(result);
    }

    private static ListMultimap<String, IdentifierLinkResolver> createIdentifierLinkGeneratorMap(
        @NotNull ListableBeanFactory beanFactory, @NotNull IdentifierConfig rootConfig) {

        //
        Map<String, IdentifierLinkResolver> generators = beanFactory
            .getBeansOfType(IdentifierLinkResolver.class)
            .values()
            .stream()
            .collect(Collectors.toMap(g -> g.getClass().getName(), Function.identity()));

        //
        ListMultimap<String, IdentifierLinkResolver> result = ArrayListMultimap.create();
        for (String className : rootConfig.getLinkResolvers()) {
            IdentifierLinkResolver generator = generators.get(className);
            if (generator == null) {
                throw new RuntimeException("Not found: " + className);
            }

            for (String prefix : generator.getSupportedIdentifierPrefixes()) {
                result.put(prefix, generator);
            }
        }

        return ImmutableListMultimap.copyOf(result);
    }

    private static Map<String, IdentifierDataset> createIdentifierDatasetMap(
        @NotNull IdentifierConfig rootConfig) {

        return rootConfig
            .getDatasets()
            .stream()
            .map(IdentifierDatasetImpl::new)
            .collect(Collectors.toConcurrentMap(IdentifierDatasetImpl::getId, Function.identity()));
    }

}
