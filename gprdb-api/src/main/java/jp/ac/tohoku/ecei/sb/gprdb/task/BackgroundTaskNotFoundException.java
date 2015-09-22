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

package jp.ac.tohoku.ecei.sb.gprdb.task;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * An exception which is thrown then a task with a specified ID is not found.
 *
 * @author Shu Tadaka
 */
public class BackgroundTaskNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes an instance of {@link BackgroundTaskNotFoundException}
     * with task ID which causes the error.
     *
     * @param id    task ID
     */
    public BackgroundTaskNotFoundException(@NotEmpty String id) {
        super(String.format("background task not found: %s", id));
    }

}
