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

import jp.ac.tohoku.ecei.sb.gprdb.app.SpringApplicationUtils;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import org.springframework.boot.SpringApplication;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class ServerTest {

    ///
    /// constructor
    ///

    @Test
    public void ensure_constructor_is_private() throws Exception {
        Constructor<Server> constructor = Server.class.getDeclaredConstructor();
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
        Server.main(arguments);

        //
        new Verifications() {{
            springApplication.run(arguments); times = 1;
        }};
    }

}
