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

package jp.ac.tohoku.ecei.sb.gprdb.app;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * CLI command.
 *
 * @author Shu Tadaka
 */
public interface CliCommand {

    /**
     * Registers a task.
     *
     * @param subparsers    parent parser
     * @return  ID of registered task
     */
    @NotNull
    Optional<String> register(@NotNull Subparsers subparsers);

    /**
     * Executes a task.
     *
     * @param arguments             command line arguments
     * @param applicationContext    application context
     * @throws Exception    an error happened during execution
     */
    void execute(@NotNull Namespace arguments, @NotNull ApplicationContext applicationContext)
            throws Exception;

}
