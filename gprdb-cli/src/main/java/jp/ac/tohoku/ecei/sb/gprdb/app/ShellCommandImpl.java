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

import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationManager;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of {@link CliCommand} to run groovy shell.
 *
 * @author Shu Tadaka
 */
@Component
class ShellCommandImpl implements CliCommand {

    private static final String COMMAND = "shell";

    @Override
    public Optional<String> register(@NotNull Subparsers subparsers) {
        subparsers.addParser(COMMAND);
        return Optional.of(COMMAND);
    }

    @Override
    public void execute(@NotNull Namespace arguments, @NotNull ApplicationContext applicationContext) {
        //
        ImmutableMap<String, Class<?>> beans = ImmutableMap.<String, Class<?>>builder()
            .put("genePairRelationManager", GenePairRelationManager.class)
            .put("identifierManager", IdentifierManager.class)
            .build();

        //
        Binding binding = new Binding();
        for (Map.Entry<String, Class<?>> entry : beans.entrySet()) {
            binding.setProperty(entry.getKey(), applicationContext.getBean(entry.getValue()));
        }

        //
        Groovysh shell = newGroovysh(binding);
        shell.run("");
    }

    @NotNull
    private static Groovysh newGroovysh(@NotNull Binding binding) {
        // This method is for unit testing. See {@link ShellCommandImplTest#test_execute}.
        return new Groovysh(binding, new IO());
    }

}
