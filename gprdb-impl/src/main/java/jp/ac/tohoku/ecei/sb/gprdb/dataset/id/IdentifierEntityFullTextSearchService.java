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

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.MustJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A service to perform full-text search on identifier entities.
 *
 * @author Shu Tadaka
 */
@Repository
class IdentifierEntityFullTextSearchService {

    private final FullTextEntityManager entityManager;

    @Autowired
    public IdentifierEntityFullTextSearchService(@NotNull EntityManager entityManager) {
        this.entityManager = Search.getFullTextEntityManager(entityManager);
    }

    @SuppressWarnings("unchecked")
    public List<IdentifierInfoEntity> searchIdentifier(
        @NotNull String keywords, boolean performAndQuery, @NotEmpty String... fields) {

        QueryBuilder builder = this.entityManager
            .getSearchFactory()
            .buildQueryBuilder()
            .forEntity(IdentifierInfoEntity.class)
            .get();

        Query query = performAndQuery
            ? buildAndQuery(builder, fields, keywords.split("\\s+"))
            : buildOrQuery(builder, fields, keywords.split("\\s+"));

        return (List<IdentifierInfoEntity>)this.entityManager
            .createFullTextQuery(query, IdentifierInfoEntity.class)
            .getResultList();
    }

    private static Query buildAndQuery(
        @NotNull QueryBuilder builder, @NotEmpty String[] fields, @NotNull String[] keywords) {

        List<Query> queries = Arrays.stream(keywords)
            .map(k -> builder.keyword().onFields(fields).matching(k + "~").createQuery())
            .collect(Collectors.toList());

        MustJunction junction = null;
        for (Query query : queries) {
            if (junction == null) {
                junction = builder.bool().must(query);
            } else {
                junction = junction.must(query);
            }
        }

        return junction.createQuery();
    }

    private static Query buildOrQuery(
        @NotNull QueryBuilder builder, @NotEmpty String[] fields, @NotNull String[] keywords) {

        return builder.keyword().onFields(fields).matching(keywords).createQuery();
    }

    public void rebuildIndexes() throws InterruptedException {
        this.entityManager.createIndexer(IdentifierInfoEntity.class).purgeAllOnStart(true).startAndWait();
        this.entityManager.createIndexer(IdentifierMappingEntity.class).purgeAllOnStart(true).startAndWait();
    }

}
