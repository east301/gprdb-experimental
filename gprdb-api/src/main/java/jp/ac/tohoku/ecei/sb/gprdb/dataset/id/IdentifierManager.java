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

import com.google.common.collect.ListMultimap;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Manages {@link Identifier}s in internal datastore.
 *
 * @author Shu Tadaka
 */
public interface IdentifierManager {

    /**
     * Gets datasets.
     *
     * @return  dataset
     */
    @NotNull
    List<IdentifierDataset> getDatasets();

    /**
     * Gets dataset.
     *
     * @param id    ID
     * @return dataset
     */
    @NotNull
    IdentifierDataset getDataset(@NotEmpty String id);

    /**
     * Gets link generators.
     *
     * @return  link generators
     */
    @NotNull
    ListMultimap<String, IdentifierLinkResolver> getLinkResolvers();

    /**
     * Wraps string ID as an instance of {@link Identifier}.
     *
     * @param id    source ID
     * @return  {@link Identifier}
     */
    @NotNull
    Identifier wrap(@NotEmpty  String id);

    /**
     * Creates a new importer.
     *
     * @param dataset   target dataset
     * @return  importer
     */
    @NotNull
    IdentifierImporter newImporter(@NotNull IdentifierDataset dataset);

}
