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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility functions to manipulate files.
 *
 * @author Shu Tadaka
 */
@Slf4j
public final class FileUtils {

    private FileUtils() {
        // do nothing
    }

    /**
     * Opens a file to read.
     *
     * @param file  file
     * @return  reader
     * @throws IOException  failed to open a file
     */
    public static BufferedReader newBufferedReader(@NotNull File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        try {
            if (file.getName().endsWith(".gz")) {
                stream = new GzipCompressorInputStream(stream);
            } else if (file.getName().endsWith(".bz2")) {
                stream = new BZip2CompressorInputStream(stream);
            }
            return new BufferedReader(new InputStreamReader(stream));

        } catch (IOException rootException) {
            try {
                stream.close();
            } catch (IOException innerException) {
                log.warn(null, innerException);
            }

            throw rootException;
        }
    }

}
