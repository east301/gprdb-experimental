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

package jp.ac.tohoku.ecei.sb.gprdb.dataset.relation;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import jp.ac.tohoku.ecei.sb.gprdb.io.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

/**
 * @author Shu Tadaka
 */
@Component
class GenePairRelationAccessorFactoryImpl implements GenePairRelationAccessorFactory {

    private final IdentifierManager identifierManager;
    private final StorageManager storageManager;

    @Autowired
    public GenePairRelationAccessorFactoryImpl(
            @NotNull IdentifierManager identifierManager, @NotNull StorageManager storageManager) {

        this.identifierManager = identifierManager;
        this.storageManager = storageManager;
    }

    @Override
    public GenePairRelationReader newReader(@NotNull String id) {
        return new GenePairRelationReaderImpl(
                HDF5Factory.openForReading(getFile(id)), this.identifierManager);
    }

    @Override
    public GenePairRelationWriter newWriter(
            @NotNull String id, @NotNull List<GenePairRelationType> types, @NotNull List<Identifier> genes) {

        return new GenePairRelationWriterImpl(HDF5Factory.open(getFile(id)), types, genes);
    }

    private File getFile(@NotNull String id) {
        return this.storageManager.getFile(id + ".hdf5");
    }

}
