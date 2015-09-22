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

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
class ThreadBackgroundTaskExecutionImpl<T> implements BackgroundTaskExecution<T>, Runnable {

    private final String id;
    private final ApplicationContext applicationContext;
    private final Class<BackgroundTask<T>> taskClass;
    private final Optional<Object> config;

    private BackgroundTaskState state = BackgroundTaskState.PENDING;
    private Optional<T> result = Optional.empty();
    private Exception exception = null;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public BackgroundTaskState getState() {
        return this.state;
    }

    @Override
    public Optional<T> getResult() {
        return this.result;
    }

    @Override
    public Optional<Exception> getException() {
        return Optional.ofNullable(this.exception);
    }

    @Override
    public void run() {
        try {
            this.state = BackgroundTaskState.PENDING;
            this.result = this.applicationContext.getBean(taskClass).execute(this.config);
        } catch (Exception ex) {
            this.exception = ex;
        } finally {
            this.state = BackgroundTaskState.FINISHED;
        }
    }

}
