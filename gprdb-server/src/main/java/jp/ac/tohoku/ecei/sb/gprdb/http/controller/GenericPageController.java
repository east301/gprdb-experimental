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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jp.ac.tohoku.ecei.sb.gprdb.meta.BuildInfoManager;
import jp.ac.tohoku.ecei.sb.gprdb.http.WebPageUrlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Shu Tadaka
 */
@Controller
@ConditionalOnWebApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GenericPageController extends AbstractController implements WebPageUrlProvider {

    private final BuildInfoManager buildInfoManager;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getWelcome() {
        return new ModelAndView("generic-welcome");
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public ModelAndView getAbout() {
        return new ModelAndView("generic-about", ImmutableMap.of(
            "build", this.buildInfoManager.getBuildInfo(),
            "dependencies", this.buildInfoManager.getDependencyLibraries()
        ));
    }

    @Override
    public Iterable<String> getCacheablePages() {
        return ImmutableList.of("/", "/about");
    }

}
