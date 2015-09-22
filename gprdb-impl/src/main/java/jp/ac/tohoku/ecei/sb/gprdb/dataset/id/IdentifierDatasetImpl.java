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

import lombok.RequiredArgsConstructor;
import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleIRI;

import java.util.Map;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class IdentifierDatasetImpl implements IdentifierDataset {

    private final IdentifierConfig.DatasetConfig config;

    @Override
    public String getId() {
        return this.config.getId();
    }

    @Override
    public String getName() {
        return this.config.getName();
    }

    @Override
    public String getShortName() {
        return this.config.getShortName();
    }

    @Override
    public IRI getUri() {
        return new SimpleIRI(this.config.getUri());
    }

    @Override
    public String getVersion() {
        return this.config.getVersion();
    }

    @Override
    public String getLoaderFactoryClass() {
        return this.config.getLoaderFactoryClass();
    }

    @Override
    public Map<String, Object> getLoaderConfig() {
        return this.config.getLoaderConfig();
    }

}
