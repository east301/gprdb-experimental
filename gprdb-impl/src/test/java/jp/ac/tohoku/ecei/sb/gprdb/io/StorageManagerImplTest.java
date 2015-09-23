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

import com.google.common.base.StandardSystemProperty;
import com.google.common.base.Strings;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.springframework.context.event.ContextRefreshedEvent;
import org.testng.annotations.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
public class StorageManagerImplTest {

    ///
    /// setup
    ///

    @Mocked
    private ContextRefreshedEvent event;

    private static boolean isWindows() {
        return Strings.nullToEmpty(StandardSystemProperty.OS_NAME.value()).toLowerCase().contains("win");
    }

    ///
    /// getFile
    ///

    @Test
    public void getFile_returns_correct_result__windows() {
        if (isWindows()) {
            StorageManagerImpl manager = new StorageManagerImpl(new File("C:\\root"), true);
            assertThat(manager.getFile("foo", "bar", "baz").getAbsolutePath()).isEqualTo("C:\\root\\foo\\bar\\baz");
        }
    }

    @Test
    public void getFile_returns_correct_result__unix() {
        if (!isWindows()) {
            StorageManagerImpl manager = new StorageManagerImpl(new File("/root"), true);
            assertThat(manager.getFile("foo", "bar", "baz").getAbsolutePath()).isEqualTo("/root/foo/bar/baz");
        }
    }

    @Test
    public void getFile_returns_correct_result__special_directory_name__windows() {
        if (isWindows()) {
            StorageManagerImpl manager = new StorageManagerImpl(new File("C:\\root"), true);
            assertThat(manager.getFile("foo", "..", "bar").getAbsolutePath()).isEqualTo("C:\\root\\bar");
        }
    }

    @Test
    public void getFile_returns_correct_result__special_directory_name__unix() {
        if (!isWindows()) {
            StorageManagerImpl manager = new StorageManagerImpl(new File("/root"), true);
            assertThat(manager.getFile("foo", "..", "bar").getAbsolutePath()).isEqualTo("/root/bar");
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void getFile_returns_correct_result__outside_of_storage__windows() {
        if (isWindows()) {
            StorageManagerImpl manager = new StorageManagerImpl(new File("C:\\root"), true);
            manager.getFile("foo", "..", "..", "...", "bar");
        } else {
            throw new RuntimeException("skipped");
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void getFile_returns_correct_result__outside_of_storage__unix() {
        if (!isWindows()) {
            StorageManagerImpl manager = new StorageManagerImpl(new File("/root"), true);
            manager.getFile("foo", "..", "..", "...", "bar");
        } else {
            throw new RuntimeException("skipped");
        }
    }


    ///
    /// isReadOnlyModePreferred
    ///

    @Test
    public void isReadOnlyModePreferred_returns_correct_result__true(@Mocked File storageRoot) {
        assertThat(new StorageManagerImpl(storageRoot, true).isReadOnlyModePreferred()).isTrue();
    }

    @Test
    public void isReadOnlyModePreferred_returns_correct_result__false(@Mocked File storageRoot) {
        assertThat(new StorageManagerImpl(storageRoot, false).isReadOnlyModePreferred()).isFalse();
    }

    ///
    /// onApplicationEvent
    ///

    @Test
    public void test_onApplicationEvent__storage_exists(@Mocked File storageRoot) {
        new NonStrictExpectations() {{
            storageRoot.exists(); result = true;
            storageRoot.mkdirs(); result = true;
        }};

        new StorageManagerImpl(storageRoot, false).onApplicationEvent(event);

        new Verifications() {{
            storageRoot.exists(); times = 1;
            storageRoot.mkdirs(); times = 0;
        }};
    }

    @Test
    public void test_onApplicationEvent__storage_not_exists__success_to_mkdirs(@Mocked File storageRoot) {
        new NonStrictExpectations() {{
            storageRoot.exists(); result = false;
            storageRoot.mkdirs(); result = true;
        }};

        new StorageManagerImpl(storageRoot, false).onApplicationEvent(event);

        new Verifications() {{
            storageRoot.exists(); times = 1;
            storageRoot.mkdirs(); times = 1;
        }};
    }

    @Test
    public void test_onApplicationEvent__storage_not_exists__fails_to_mkdirs(@Mocked File storageRoot) {
        new NonStrictExpectations() {{
            storageRoot.exists(); result = false;
            storageRoot.mkdirs(); result = false;
        }};

        new StorageManagerImpl(storageRoot, false).onApplicationEvent(event);

        new Verifications() {{
            storageRoot.exists(); times = 1;
            storageRoot.mkdirs(); times = 1;
        }};
    }

    @Test
    public void test_onApplicationEvent__readOnlyPreferred(@Mocked File storageRoot) {
        new NonStrictExpectations() {{
            storageRoot.exists(); result = false;
            storageRoot.mkdirs(); result = false;
        }};

        new StorageManagerImpl(storageRoot, true).onApplicationEvent(event);

        new Verifications() {{
            storageRoot.exists(); times = 0;
            storageRoot.mkdirs(); times = 0;
        }};
    }

}
