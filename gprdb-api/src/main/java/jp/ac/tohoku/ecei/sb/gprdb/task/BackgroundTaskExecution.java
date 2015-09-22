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

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Provides access to task execution state.
 *
 * @param <T> type of result of background task
 *
 * @author Shu Tadaka
 */
public interface BackgroundTaskExecution<T> {

    /**
     * Gets execution ID.
     *
     * @return  ID
     */
    @NotEmpty
    String getId();

    /**
     * Gets state.
     *
     * @return  state
     */
    @NotNull
    BackgroundTaskState getState();

    /**
     * Gets result.
     *
     * @return  result
     */
    @NotNull
    Optional<T> getResult();

    /**
     * Gets exception thrown during the execution of the task.
     *
     * @return  exception
     */
    @NotNull
    Optional<Exception> getException();

}
