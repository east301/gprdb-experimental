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

package jp.ac.tohoku.ecei.sb.gprdb;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import jp.ac.tohoku.ecei.sb.gprdb.app.CliCommand;
import jp.ac.tohoku.ecei.sb.gprdb.app.SpringApplicationUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Slf4j
public final class Cli {

    private Cli() {
        // do nothing
    }

    /**
     * Entry point of the program.
     *
     * @param args  command line arguments
     */
    @SneakyThrows
    public static void main(String... args) {
        try (ConfigurableApplicationContext applicationContext = newApplicationContext(args)) {
            runCommand(applicationContext, args);
        }
    }

    private static ConfigurableApplicationContext newApplicationContext(@NotNull String[] args) {
        //
        System.setProperty("SERVER_PORT", "0");

        //
        SpringApplication application = SpringApplicationUtils.newApplication();
        application.setWebEnvironment(isWebEnvironmentRequested());
        return application.run(args);
    }

    private static boolean isWebEnvironmentRequested() {
        String value = System.getProperty("SPRING_MAIN_WEB_ENVIRONMENT");
        return !Strings.isNullOrEmpty(value) || Boolean.valueOf(value);
    }

    @SneakyThrows
    @SuppressWarnings("rawtypes")
    private static void runCommand(@NotNull ApplicationContext applicationContext, @NotNull String[] args) {
        //
        ArgumentParser parser = ArgumentParsers.newArgumentParser("gprdb-cli");
        Subparsers subparsers = parser.addSubparsers().dest("task");

        Map<String, CliCommand> taskIdMap = Maps.newHashMap();
        for (CliCommand task : applicationContext.getBeansOfType(CliCommand.class).values()) {
            Optional<String> taskId = task.register(subparsers);
            if (taskId.isPresent()) {
                taskIdMap.put(taskId.get(), task);
            }
        }

        //
        Namespace arguments;
        try {
            arguments = parser.parseArgs(args);
        } catch (ArgumentParserException ex) {
            parser.handleError(ex);
            return;
        }

        //
        CliCommand task = taskIdMap.get(arguments.getString("task"));
        try {
            task.execute(arguments, applicationContext);
        } catch (Exception ex) {
            log.error("an error happened during execution of a task", ex);
        }
    }

}
