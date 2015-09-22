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

package jp.ac.tohoku.ecei.sb.gprdb.io;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.FileSystems;

/**
 * @author Shu Tadaka
 */
@Component
@Slf4j
class StorageManagerImpl implements StorageManager, ApplicationListener<ContextRefreshedEvent> {

    private final File storageRoot;
    private final boolean readOnlyModePreferred;

    @Autowired
    public StorageManagerImpl(
        @NotNull @Value("${gprdb.storage.path}") File storageRoot,
        @Value("${gprdb.storage.readOnlyModePreferred:false}") boolean readOnlyModePreferred) {

        this.storageRoot = storageRoot;
        this.readOnlyModePreferred = readOnlyModePreferred;
    }

    @Override
    public File getFile(@NotNull String head, String... tail) {
        //
        String[] fragments = ImmutableList.<String>builder()
            .add(head)
            .add(tail)
            .build()
            .toArray(new String[0]);

        File result = FileSystems
            .getDefault()
            .getPath(this.storageRoot.getAbsolutePath(), fragments)
            .normalize()
            .toFile();

        File root = FileSystems.getDefault().getPath(this.storageRoot.getAbsolutePath()).normalize().toFile();
        if (!result.getAbsolutePath().startsWith(root.getAbsolutePath())) {
            throw new IllegalArgumentException("Illegal access to a file or directory: " + result.getAbsolutePath());
        }

        return result;
    }

    @Override
    public boolean isReadOnlyModePreferred() {
        return this.readOnlyModePreferred;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!this.readOnlyModePreferred && !this.storageRoot.exists() && !this.storageRoot.mkdirs()) {
            log.warn("Failed to create directory for file storage: {}", this.storageRoot.getAbsolutePath());
        }
    }

}
