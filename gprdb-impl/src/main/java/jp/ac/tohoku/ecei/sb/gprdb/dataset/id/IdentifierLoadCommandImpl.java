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

import jp.ac.tohoku.ecei.sb.gprdb.app.CliCommand;
import jp.ac.tohoku.ecei.sb.gprdb.io.LoaderManager;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Component
class IdentifierLoadCommandImpl implements CliCommand {

    private static final String COMMAND_ID = "load-id";
    private static final String TARGET_KEY = "target";

    @Override
    public Optional<String> register(@NotNull Subparsers subparsers) {
        //
        Subparser parser = subparsers.addParser(COMMAND_ID);
        parser.addArgument(TARGET_KEY);

        //
        return Optional.of(COMMAND_ID);
    }

    @Override
    @Transactional
    public void execute(
        @NotNull Namespace arguments, @NotNull ApplicationContext applicationContext)
        throws Exception {

        //
        IdentifierManager identifierManager = applicationContext.getBean(IdentifierManager.class);
        IdentifierDataset dataset = identifierManager.getDataset(arguments.getString(TARGET_KEY));

        //
        LoaderManager loaderManager = applicationContext.getBean(LoaderManager.class);
        try (
            IdentifierLoader loader = loaderManager.newLoader(dataset);
            IdentifierImporter importer = identifierManager.newImporter(dataset)
        ) {
            loader.load(importer::add);
        }
    }

}
