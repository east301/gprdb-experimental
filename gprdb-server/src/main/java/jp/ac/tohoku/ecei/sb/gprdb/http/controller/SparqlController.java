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

package jp.ac.tohoku.ecei.sb.gprdb.http.controller;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import jp.ac.tohoku.ecei.sb.gprdb.coding.Ref;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Shu Tadaka
 */
@Controller
@ConditionalOnWebApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SparqlController {

    private final Repository repository;

    @RequestMapping(value = "/sparql", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getSparqlForm(@RequestParam(value = "query", required = false) String sparql) {
        return Strings.isNullOrEmpty(sparql)
            ? getSparqlForm()
            : doExecuteSparqlQueryAndShowResult(sparql);
    }

    @RequestMapping(value = "/sparql", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView executeSparqlQueryAndShowResult(@NotEmpty String sparql) {
        return doExecuteSparqlQueryAndShowResult(sparql);
    }

    @SuppressWarnings("serial")
    private ModelAndView getSparqlForm() {
        return new ModelAndView("sparql", ImmutableMap.of(
            "hasSparql", false,
            "hasResult", false,
            "hasError", false
        ));
    }

    private ModelAndView doExecuteSparqlQueryAndShowResult(@NotEmpty String sparql) {
        try (RepositoryConnection connection = this.repository.getConnection()) {
            try {
                TupleQueryResult result = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate();
                return showSparqlExecutionResult(sparql, result);
            } catch (Exception ex) {
                return showSparqlExecutionError(sparql, ex);
            }
        }
    }

    private ModelAndView showSparqlExecutionResult(@NotEmpty String sparql, @NotNull TupleQueryResult result) {
        //
        Ref<List<BindingSet>> resultBodyRef = new Ref<>();
        Ref<Integer> resultCountRef = new Ref<>();
        getBindingSets(result, 100, resultBodyRef, resultCountRef);

        //
        return new ModelAndView("sparql", ImmutableMap.<String, Object>builder()
            .put("hasSparql", true)
            .put("sparql", sparql)
            .put("hasResult", true)
            .put("resultHeader", result.getBindingNames())
            .put("resultBody", resultBodyRef.get())
            .put("resultCount", resultCountRef.get())
            .put("hasError", false)
            .build());
    }

    private void getBindingSets(
        @NotNull QueryResult<BindingSet> rawResult,
        @Min(0) int max,
        @NotNull Ref<List<BindingSet>> resultRef,
        @NotNull Ref<Integer> countRef) {

        //
        List<BindingSet> result = Lists.newArrayList();
        int count = 0;

        while (rawResult.hasNext()) {
            BindingSet next = rawResult.next();
            if (result.size() <= max) {
                result.add(next);
            }

            count++;
        }

        //
        resultRef.set(result);
        countRef.set(count);
    }

    private ModelAndView showSparqlExecutionError(@NotNull String sparql, @NotNull Exception ex) {
        return new ModelAndView("sparql", ImmutableMap.<String, Object>builder()
            .put("hasSparql", sparql)
            .put("sparql", sparql)
            .put("hasResult", false)
            .put("hasError", true)
            .put("error", ex)
            .build());
    }

}
