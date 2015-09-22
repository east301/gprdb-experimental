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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStore;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.hibernate.validator.constraints.NotEmpty;
import org.openrdf.model.IRI;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Shu Tadaka
 */
@Value
@EqualsAndHashCode(of = "id")
class IdentifierImpl implements Identifier {

    @JsonIgnore
    private final IdentifierInternalSharedData sharedData;

    @JsonIgnore
    private final String id;

    @Override
    @JsonProperty("id")
    public String getId() {
        return this.id;
    }

    @Override
    @JsonIgnore
    public String getPrefix() {
        return this.id.substring(0, this.id.indexOf(":"));
    }

    @Override
    @JsonIgnore
    public String getBody() {
        return this.id.substring(this.id.indexOf(":") + 1);
    }

    @Override
    @JsonProperty("name")
    public Optional<String> getName() {
        return Optional.ofNullable(this.sharedData.getIdentifierNameCache().get(this.id));
    }

    @Override
    @JsonProperty("description")
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.sharedData.getIdentifierDescriptionCache().get(this.id));
    }

    @Override
    public Optional<IRI> getUri() {
        for (IdentifierUriResolver resolver : this.sharedData.getIdentifierUriResolverMap().get(getPrefix())) {
            Optional<IRI> uri = resolver.resolve(this);
            if (uri.isPresent()) {
                return uri;
            }
        }

        return Optional.empty();
    }

    @Override
    @JsonIgnore
    public Optional<IdentifierLink> getLink() {
        for (IdentifierLinkResolver generator : this.sharedData.getIdentifierLinkGeneratorMap().get(getPrefix())) {
            List<IdentifierLink> links = generator.generate(this);
            if (!links.isEmpty()) {
                return Optional.of(links.get(0));
            }
        }

        return Optional.empty();
    }

    @Override
    @JsonIgnore
    public List<Identifier> getMappingResults(@NotNull IdentifierMappingType type) {
        if (type.getType().equals(IdentifierMappingTypes.INTRA_SPECIES)) {
            return getCachedIdentifierMappingResultsInternal(
                this.sharedData.getIdentifierIntraSpeciesMappingCache(), "*");
        } else if (type.getType().equals(IdentifierMappingTypes.INTER_SPECIES)) {
            return getCachedIdentifierMappingResultsInternal(
                this.sharedData.getIdentifierInterSpeciesMappingCache(), type.getHint().get());
        } else {
            return getIdentifierMappingResultsInternal(type);
        }
    }

    private List<Identifier> getCachedIdentifierMappingResultsInternal(
        @NotNull KeyValueStore<String, String> kvs, @NotEmpty String hint) {

        String cache = kvs.get(this.id + "/" + hint);
        return cache == null
            ? Collections.emptyList()
            : Lists.transform(ImmutableList.copyOf(cache.split(";")), i -> new IdentifierImpl(this.sharedData, i));
    }

    private List<Identifier> getIdentifierMappingResultsInternal(@NotNull IdentifierMappingType type) {
        return getInfo()
            .stream()
            .flatMap(i -> i.getMappings().stream())
            .filter(m -> m.getType().equals(type))
            .map(IdentifierMapping::getTargetIdentifier)
            .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public List<IdentifierInfo> getInfo() {
        IdentifierInfoEntityRepository repository = this.sharedData.getIdentifierInfoEntityRepository();
        List<IdentifierInfoEntity> entities = repository.findAllByIdentifier(this.id);
        return Lists.transform(entities, e -> new IdentifierInfoImpl(this.sharedData, e));
    }

    @Override
    @JsonIgnore
    public List<IdentifierLink> getLinks() {
        return this.sharedData
            .getIdentifierLinkGeneratorMap()
            .get(getPrefix())
            .stream()
            .map(g -> g.generate(this))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return this.id;
    }

}
