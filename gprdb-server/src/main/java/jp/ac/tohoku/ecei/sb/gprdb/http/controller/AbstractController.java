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

import com.google.common.collect.ImmutableMap;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationManager;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Base class for request controllers.
 *
 * @author Shu Tadaka
 */
public abstract class AbstractController {

    protected static final String HTML = MediaType.TEXT_HTML_VALUE;
    protected static final String JSON = MediaType.APPLICATION_JSON_VALUE;
    protected static final String RDFXML = "application/rdf+xml";
    protected static final String N3 = "text/n3";

    @Autowired
    @Setter
    private GenePairRelationManager genePairRelationManager;

    @ModelAttribute
    public void addGlobalModelAttributes(Model model) {
        model.addAttribute("global", ImmutableMap.of(
            "datasets", this.genePairRelationManager.getDatasets(),
            "relationTypes", this.genePairRelationManager.getTypes()
        ));
    }

}
