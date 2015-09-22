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

import groovy.lang.Binding;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierManager;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationManager;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class ShellCommandImplTest {

    ///
    /// register
    ///

    @Test
    public void test_register(@Mocked Subparsers subparsers) {
        Optional<String> result = new ShellCommandImpl().register(subparsers);
        assertThat(result).isPresent().contains("shell");

        new Verifications() {{
            subparsers.addParser("shell"); times = 1;
        }};
    }

    ///
    /// execute
    ///

    @Test
    public void test_execute(
        @Mocked Groovysh shell,
        @Mocked Namespace arguments,
        @Mocked ApplicationContext applicationContext,
        @Mocked IdentifierManager identifierManager,
        @Mocked GenePairRelationManager genePairRelationManager) {

        //
        AtomicReference<Binding> specifiedBinding = new AtomicReference<>();
        new MockUp<ShellCommandImpl>() {
            @Mock
            Groovysh newGroovysh(Binding binding) {
                specifiedBinding.set(binding);
                return shell;
            }
        };

        new NonStrictExpectations() {{
            applicationContext.getBean(IdentifierManager.class); result = identifierManager;
            applicationContext.getBean(GenePairRelationManager.class); result = genePairRelationManager;
        }};

        //
        new ShellCommandImpl().execute(arguments, applicationContext);

        //
        Binding binding = specifiedBinding.get();
        assertThat(binding).isNotNull();
        assertThat(binding.getVariable("identifierManager")).isSameAs(identifierManager);
        assertThat(binding.getVariable("genePairRelationManager")).isSameAs(genePairRelationManager);

        new Verifications() {{
            shell.run(""); times = 1;
        }};

    }

}
