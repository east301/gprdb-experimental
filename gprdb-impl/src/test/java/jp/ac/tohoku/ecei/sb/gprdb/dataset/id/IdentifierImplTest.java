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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStore;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class IdentifierImplTest {

    ///
    /// setup
    ///

    @Mocked
    private IdentifierInternalSharedData sharedData;

    @Mocked
    private IdentifierInfoEntityRepository identifierInfoEntityRepository;

    @Mocked
    private ListMultimap<String, IdentifierLinkResolver> linkGeneratorMap;

    @Mocked
    private KeyValueStore<String, String> nameCache;

    @Mocked
    private KeyValueStore<String, String> descriptionCache;

    @BeforeMethod
    public void setUp() {
        new NonStrictExpectations() {{
            sharedData.getIdentifierInfoEntityRepository(); result = identifierInfoEntityRepository;
            sharedData.getIdentifierLinkGeneratorMap(); result = linkGeneratorMap;
            sharedData.getIdentifierNameCache(); result = nameCache;
            sharedData.getIdentifierDescriptionCache(); result = descriptionCache;
        }};
    }

    ///
    /// getId
    ///

    @Test
    public void getId_returns_correct_result() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getId()).isEqualTo("foo:bar");
    }

    ///
    /// getPrefix
    ///

    @Test
    public void getPrefix_returns_correct_result__single_colon() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getPrefix()).isEqualTo("foo");
    }

    @Test
    public void getPrefix_returns_correct_result__multiple_colons() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar:baz");
        assertThat(identifier.getPrefix()).isEqualTo("foo");
    }

    ///
    /// getBody
    ///

    @Test
    public void getBody_returns_correct_result__single_colon() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getBody()).isEqualTo("bar");
    }

    @Test
    public void getBody_returns_corect_result__multiple_colon() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar:baz");
        assertThat(identifier.getBody()).isEqualTo("bar:baz");
    }

    ///
    /// getName
    ///

    @Test
    public void getName_returns_name_from_cache() {
        new NonStrictExpectations() {{
            nameCache.get("foo:bar"); result = "hoge-piyo";
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getName()).isPresent().contains("hoge-piyo");

        new Verifications() {{
            nameCache.get("foo:bar"); times = 1;
        }};
    }

    @Test
    public void getName_returns_empty_if_name_is_not_cached() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getName()).isEmpty();

        new Verifications() {{
            nameCache.get("foo:bar"); times = 1;
        }};
    }

    ///
    /// getDescription
    ///

    @Test
    public void getDescription_returns_description_from_cache() {
        new NonStrictExpectations() {{
            descriptionCache.get("foo:bar"); result = "hoge-piyo";
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getDescription()).isPresent().contains("hoge-piyo");

        new Verifications() {{
            descriptionCache.get("foo:bar"); times = 1;
        }};
    }

    @Test
    public void getDescription_returns_empty_if_description_is_not_cached() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getDescription()).isEmpty();

        new Verifications() {{
            descriptionCache.get("foo:bar"); times = 1;
        }};
    }

    ///
    /// getLink
    ///

    @Test
    public void getLink_returns_correct_result__001(
        @Mocked IdentifierLinkResolver generator, @Mocked IdentifierLink link) {

        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = ImmutableList.of(generator);
            generator.generate((Identifier)any); result = ImmutableList.of(link);
            link.getUrl(); result = "http://example.com/bar";
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getLink()).isPresent();
        assertThat(identifier.getLink().get()).isSameAs(link);
    }

    @Test
    public void getLink_returns_correct_result__002(
        @Mocked IdentifierLinkResolver generator, @Mocked IdentifierLink link1, @Mocked IdentifierLink link2) {

        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = ImmutableList.of(generator);
            generator.generate((Identifier)any); result = ImmutableList.of(link1, link2);
            link1.getUrl(); result = "http://example.com/bar";
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getLink()).isPresent();
        assertThat(identifier.getLink().get()).isSameAs(link1);
    }

    @Test
    public void getLink_returns_correct_result__003(@Mocked IdentifierLinkResolver generator) {
        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = ImmutableList.of(generator);
            generator.generate((Identifier)any); result = ImmutableList.of();
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getLink()).isEmpty();
    }

    @Test
    public void getLink_returns_correct_result__004(
        @Mocked IdentifierLinkResolver generator1,
        @Mocked IdentifierLinkResolver generator2,
        @Mocked IdentifierLink link) {

        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = ImmutableList.of(generator1, generator2);
            generator1.generate((Identifier) any); result = ImmutableList.of(link);
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getLink()).isPresent();
        assertThat(identifier.getLink().get()).isSameAs(link);
    }

    ///
    /// getInfo
    ///

    @Test
    public void getInfo_returns_correct_result(
        @Mocked IdentifierInfoEntity info1, @Mocked IdentifierInfoEntity info2) {

        new NonStrictExpectations() {{
            identifierInfoEntityRepository.findAllByIdentifier("foo:bar");
            result = ImmutableList.of(info1, info2);
            info1.getName();
            result = "info1";
            info2.getName();
            result = "info2";
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        List<IdentifierInfo> info = identifier.getInfo();

        assertThat(info).hasSize(2);
        assertThat(info.get(0).getName()).isPresent().contains("info1");
        assertThat(info.get(1).getName()).isPresent().contains("info2");

        new Verifications() {{
            identifierInfoEntityRepository.findAllByIdentifier("foo:bar"); times = 1;
            info1.getName(); times = 1;
            info2.getName(); times = 1;
        }};
    }

    @Test
    public void getInfo_returns_correct_result__no_information() {
        new NonStrictExpectations() {{
            identifierInfoEntityRepository.findAllByIdentifier("foo:bar");
            result = Collections.emptyList();
        }};

        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.getInfo()).isNotNull().isEmpty();

        new Verifications() {{
            identifierInfoEntityRepository.findAllByIdentifier("foo:bar"); times = 1;
        }};
    }

    ///
    /// getLinks
    ///

    @Test
    public void getLinks_returns_correct_result__001(
        @Mocked IdentifierLinkResolver generator1,
        @Mocked IdentifierLink link1) {

        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = ImmutableList.of(generator1);
            generator1.generate((Identifier)any); result = ImmutableList.of(link1);
        }};

        IdentifierImpl identifier = new IdentifierImpl(this.sharedData, "foo:bar");
        List<IdentifierLink> links = identifier.getLinks();
        assertThat(links).isNotNull().hasSize(1).containsSequence(link1);
    }

    @Test
    public void getLinks_returns_correct_result__002(
        @Mocked IdentifierLinkResolver generator1,
        @Mocked IdentifierLink link11,
        @Mocked IdentifierLink link12) {

        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = ImmutableList.of(generator1);
            generator1.generate((Identifier)any); result = ImmutableList.of(link11, link12);
        }};

        IdentifierImpl identifier = new IdentifierImpl(this.sharedData, "foo:bar");
        List<IdentifierLink> links = identifier.getLinks();
        assertThat(links).isNotNull().hasSize(2).containsSequence(link11, link12);
    }

    @Test
    public void getLinks_returns_correct_result__003(
        @Mocked IdentifierLinkResolver generator1,
        @Mocked IdentifierLinkResolver generator2,
        @Mocked IdentifierLink link11,
        @Mocked IdentifierLink link12,
        @Mocked IdentifierLink link21) {

        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = ImmutableList.of(generator1, generator2);
            generator1.generate((Identifier) any); result = ImmutableList.of(link11, link12);
            generator2.generate((Identifier)any); result = ImmutableList.of(link21);
        }};

        IdentifierImpl identifier = new IdentifierImpl(this.sharedData, "foo:bar");
        List<IdentifierLink> links = identifier.getLinks();
        assertThat(links).isNotNull().hasSize(3).containsSequence(link11, link12, link21);
    }

    @Test
    public void getLinks_returns_correct_result__004() {
        new NonStrictExpectations() {{
            linkGeneratorMap.get("foo"); result = Collections.emptyList();
        }};

        IdentifierImpl identifier = new IdentifierImpl(this.sharedData, "foo:bar");
        List<IdentifierLink> links = identifier.getLinks();
        assertThat(links).isNotNull().isEmpty();
    }

    ///
    /// toString
    ///

    @Test
    public void toString_returns_id_itself() {
        IdentifierImpl identifier = new IdentifierImpl(sharedData, "foo:bar");
        assertThat(identifier.toString()).isEqualTo("foo:bar");
    }

    ///
    /// equals & hashCode
    ///

    @Test
    public void equals_and_hashCode_are_correctly_implemented() {
        IdentifierImpl identifier1 = new IdentifierImpl(sharedData, "foo:bar");
        IdentifierImpl identifier2 = new IdentifierImpl(sharedData, "foo:bar");
        IdentifierImpl identifier3 = new IdentifierImpl(sharedData, "foo:baz");

        assertThat(identifier1).isEqualTo(identifier1).isEqualTo(identifier2).isNotEqualTo(identifier3);
        assertThat(identifier2).isEqualTo(identifier1).isEqualTo(identifier2).isNotEqualTo(identifier3);
        assertThat(identifier3).isNotEqualTo(identifier1).isNotEqualTo(identifier2).isEqualTo(identifier3);

        assertThat(identifier1.hashCode()).isEqualTo(identifier2.hashCode());
    }

}
