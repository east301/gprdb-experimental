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

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of {@link BackgroundTaskManager} which uses thread-pool as backend.
 *
 * @author Shu Tadaka
 */
@Component
@Slf4j
class ThreadBackgroundTaskManagerImpl implements BackgroundTaskManager {

    private final ApplicationContext applicationContext;
    private final ThreadPoolTaskExecutor executor;
    private final ConcurrentMap<String, BackgroundTaskExecution<?>> queue;

    @Autowired
    public ThreadBackgroundTaskManagerImpl(
        @NotNull ApplicationContext applicationContext,
        @Value("${gprdb.task.threadPool.concurrency:-1}") int concurrency) {

        this.applicationContext = applicationContext;
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.setCorePoolSize(1);
        this.executor.setMaxPoolSize(getConcurrency(concurrency));
        this.executor.initialize();
        this.queue = Maps.newConcurrentMap();
    }

    @PreDestroy
    public void shutdownThreadPool() {
        this.executor.shutdown();
    }

    @Min(1)
    private static int getConcurrency(int numThreads) {
        //
        if (numThreads >= 1) {
            return numThreads;
        }

        //
        int numCores = Runtime.getRuntime().availableProcessors();
        log.warn(
            "`gprdb.task.threadPool.concurrency` is not set or invalid. falling back to {}",
            numCores);

        return numCores;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> BackgroundTaskExecution<T> get(@NotEmpty String id) {
        BackgroundTaskExecution<T> execution = (BackgroundTaskExecution<T>)this.queue.get(id);
        if (execution == null) {
            throw new BackgroundTaskNotFoundException(id);
        }

        return execution;
    }

    @Override
    public Collection<BackgroundTaskExecution<?>> getAll() {
        return this.queue.values();
    }

    @Override
    public <T> BackgroundTaskExecution<T> execute(
        @NotNull Class<BackgroundTask<T>> taskClass, @NotNull Optional<Object> config) {

        String id = UUID.randomUUID().toString();
        ThreadBackgroundTaskExecutionImpl<T> execution = new ThreadBackgroundTaskExecutionImpl<>(
            id, this.applicationContext, taskClass, config);

        this.executor.execute(execution);
        this.queue.put(id, execution);

        return execution;
    }

}
