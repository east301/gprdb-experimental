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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Identifier dataset configurations.
 *
 * @author Shu Tadaka
 */
@Component
@ConfigurationProperties(prefix = "gprdb.identifier")
@Data
class IdentifierConfig {

    @Data
    @EqualsAndHashCode(of = "id")
    public static class DatasetConfig {
        private String id;
        private String name;
        private String shortName;
        private String uri;
        private String version;
        private String loaderFactoryClass;
        private Map<String, Object> loaderConfig = Maps.newHashMap();
    }

    private List<DatasetConfig> datasets = Lists.newArrayList();
    private List<String> uriResolvers = Lists.newArrayList();
    private List<String> linkResolvers = Lists.newArrayList();

}
