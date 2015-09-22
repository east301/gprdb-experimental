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

import jp.ac.tohoku.ecei.sb.gprdb.app.CliCommand;
import jp.ac.tohoku.ecei.sb.gprdb.io.StorageManager;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Component
class TripleStoreBuildCommandImpl implements CliCommand {

    private static final String COMMAND = "build-triple-store";

    @Override
    public Optional<String> register(@NotNull Subparsers subparsers) {
        subparsers.addParser(COMMAND);
        return Optional.of(COMMAND);
    }

    @Override
    public void execute(
        @NotNull Namespace arguments, @NotNull ApplicationContext applicationContext)
        throws Exception {

        //
        StorageManager manager = applicationContext.getBean(StorageManager.class);
        GenePairRelationExporter exporter = applicationContext.getBean(GenePairRelationExporter.class);

        //
        File repositoryDirectory = manager.getFile("triple-store");
        if (!repositoryDirectory.exists() && !repositoryDirectory.mkdirs()) {
            throw new RuntimeException("Failed to create a new directory: " + repositoryDirectory.getAbsolutePath());
        }

        Repository repository = new SailRepository(new NativeStore(repositoryDirectory));
        repository.initialize();

        //
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.begin();
            connection.add(exporter.getDatasetsAsRdfV1());
            connection.commit();
        }
    }

}
