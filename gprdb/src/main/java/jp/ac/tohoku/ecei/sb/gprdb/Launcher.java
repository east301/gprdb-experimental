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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Launches gprdb services.
 *
 * @author Shu Tadaka
 */
public final class Launcher {

    private static final Map<String, Consumer<String[]>> ENTRY_POINTS = ImmutableMap.of(
        "cli", Cli::main,
        "server", Server::main
    );

    private Launcher() {
        // do nothing
    }

    /**
     * Entry point of the program.
     *
     * @param args  command line arguments
     */
    public static void main(String... args) {
        //
        if (args.length < 1 || !ENTRY_POINTS.containsKey(args[0])) {
            System.err.printf("usage: gprdb {%s} ...\n", Joiner.on(",").join(ENTRY_POINTS.keySet()));
            System.err.printf("gprdb: error: too few arguments, or invalid action name\n");
            return;
        }

        //
        ENTRY_POINTS.get(args[0]).accept(Arrays.stream(args).skip(1).toArray(String[]::new));
    }

}
