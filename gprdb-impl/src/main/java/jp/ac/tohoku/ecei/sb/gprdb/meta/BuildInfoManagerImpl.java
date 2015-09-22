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

package jp.ac.tohoku.ecei.sb.gprdb.meta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author Shu Tadaka
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@SuppressWarnings("unchecked")
class BuildInfoManagerImpl implements BuildInfoManager {

    private final ObjectMapper objectMapper;

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final BuildInfo cachedBuildInfo = loadBuildInfo();

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final List<DependencyLibrary> cachedDependencyLibraries = loadDependencyLibraries();

    private BuildInfo loadBuildInfo() {
        //
        Properties properties = new Properties();
        try (InputStream stream = Resources.getResource("gprdb-build.properties").openStream()) {
            properties.load(stream);
        } catch (Exception ex) {
            log.warn("failed to load /gprdb-build.properties from resource");
        }

        //
        return new BuildInfoImpl(properties);
    }

    private List<DependencyLibrary> loadDependencyLibraries() {
        //
        CollectionType type = this.objectMapper
            .getTypeFactory()
            .constructCollectionType(List.class, DependencyLibraryImpl.class);

        //
        try (InputStream stream = Resources.getResource("gprdb-dependency-license.json").openStream()) {
            return Lists.transform(this.objectMapper.readValue(stream, type), DependencyLibrary.class::cast);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.warn("failed to load /gprdb-dependency-license.json from resource");
            return Collections.emptyList();
        }
    }

    @Override
    public BuildInfo getBuildInfo() {
        return getCachedBuildInfo();
    }

    @Override
    public List<DependencyLibrary> getDependencyLibraries() {
        return getCachedDependencyLibraries();
    }

}
