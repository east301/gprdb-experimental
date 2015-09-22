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

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableMap;
import jp.ac.tohoku.ecei.sb.gprdb.app.CliCommand;
import jp.ac.tohoku.ecei.sb.gprdb.app.SpringApplicationUtils;
import lombok.RequiredArgsConstructor;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class CliTest {

    ///
    /// constructor
    ///

    @Test
    public void constructor_is_private() throws Exception {
        //
        Constructor<Cli> constructor = Cli.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible()).isFalse();

        constructor.setAccessible(true);
        assertThat(constructor.newInstance()).isNotNull();
    }

    ///
    /// main
    ///

    @Test
    public void ensure_spring_application_is_started_in_main(@Mocked SpringApplication springApplication) {
        //
        new MockUp<SpringApplicationUtils>() {
            @Mock
            SpringApplication newApplication() {
                return springApplication;
            }
        };

        //
        String[] arguments = new String[]{"foo", "bar", "baz"};
        Cli.main(arguments);

        //
        new Verifications() {{
            springApplication.setWebEnvironment(false); times = 1;
            springApplication.run(arguments); times = 1;
        }};
    }

    @Test
    public void ensure_specified_command_is_executed(
        @Mocked SpringApplication springApplication,
        @Mocked ConfigurableApplicationContext applicationContext) {

        //
        List<String> executions = Lists.newArrayList();

        new MockUp<SpringApplicationUtils>() {
            @Mock
            SpringApplication newApplication() {
                return springApplication;
            }
        };
        new NonStrictExpectations() {{
            springApplication.run((String[])any); result = applicationContext;
            applicationContext.getBeansOfType(CliCommand.class); result = ImmutableMap.of(
                "command1", new CliCommandImpl(Optional.of("command1"), executions),
                "command2", new CliCommandImpl(Optional.empty(), executions)
            );
        }};

        //
        Cli.main("command1");

        //
        new Verifications() {{
            springApplication.setWebEnvironment(false); times = 1;
            springApplication.run((String[])any); times = 1;
            applicationContext.getBeansOfType(CliCommand.class); times = 1;
        }};
    }

    @RequiredArgsConstructor
    private static class CliCommandImpl implements CliCommand {

        private final Optional<String> command;
        private final List<String> executions;

        @Override
        public Optional<String> register(@NotNull Subparsers subparsers) {
            return this.command;
        }

        @Override
        public void execute(@NotNull Namespace arguments, @NotNull ApplicationContext applicationContext) {
            this.executions.add(this.command.get());
        }

    }

}
