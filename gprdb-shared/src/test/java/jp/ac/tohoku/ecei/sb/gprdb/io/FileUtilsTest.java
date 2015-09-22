package jp.ac.tohoku.ecei.sb.gprdb.io;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Shu Tadaka
 */
@Slf4j
public class FileUtilsTest {

    ///
    /// setup
    ///

    private File temporaryDirectory;
    private String sourceText;

    private File uncompressedFile;
    private File gzipCompressedFile;
    private File bzip2CompressedFile;

    @BeforeClass
    public void setUp() throws Exception {
        //
        temporaryDirectory = Files.createTempDir();
        sourceText = Resources
            .toString(Resources.getResource(FileUtilsTest.class, "text-001.txt"), Charsets.UTF_8)
            .split("\n")[0];

        //
        uncompressedFile = new File(temporaryDirectory, "uncompressed.txt");
        gzipCompressedFile = new File(temporaryDirectory, "compressed.txt.gz");
        bzip2CompressedFile = new File(temporaryDirectory, "compressed.txt.bz2");

        createUncompressedFile(uncompressedFile);
        createCompressedFile(GzipCompressorOutputStream.class, gzipCompressedFile);
        createCompressedFile(BZip2CompressorOutputStream.class, bzip2CompressedFile);
    }

    private void createUncompressedFile(@NotNull File output) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(output)) {
            stream.write(sourceText.getBytes());
        }
    }

    private <T extends OutputStream> void createCompressedFile(
        @NotNull Class<T> compressorOutputStreamClass, @NotNull File output)
        throws Exception {

        Constructor<T> constructor = compressorOutputStreamClass.getConstructor(OutputStream.class);
        try (OutputStream stream = constructor.newInstance(new FileOutputStream(output))) {
            stream.write(sourceText.getBytes());
        }
    }

    @AfterClass
    public void tearDown() {
        File[] files = new File[]{uncompressedFile, gzipCompressedFile, bzip2CompressedFile, temporaryDirectory};
        for (File file : files) {
            if (!file.delete()) {
                log.warn("failed to delete a temporary file or directory: {}", file.getAbsolutePath());
            }
        }
    }

    ///
    /// constructor
    ///

    @Test
    public void constructor_is_private() throws Exception {
        Constructor<FileUtils> constructor = FileUtils.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible()).isFalse();

        constructor.setAccessible(true);
        assertThat(constructor.newInstance()).isNotNull();
    }

    ///
    /// newBufferedReader
    ///

    @Test
    public void newBufferedReader_constructs_correct_stream__uncompressed() throws IOException {
        verifyBehaviorOfBufferedReader(FileUtils.newBufferedReader(uncompressedFile));
        verifyBehaviorOfBufferedReader(FileUtils.newBufferedReader(gzipCompressedFile));
        verifyBehaviorOfBufferedReader(FileUtils.newBufferedReader(bzip2CompressedFile));
    }

    private void verifyBehaviorOfBufferedReader(@NotNull BufferedReader reader) throws IOException {
        assertThat(reader.readLine()).isEqualTo(sourceText);
        assertThat(reader.readLine()).isNull();
    }

    @Test(expectedExceptions = IOException.class, expectedExceptionsMessageRegExp = "error")
    public void newBufferedReader_propagates_exception__001() throws IOException {
        new MockUp<BufferedReader>() {
            @Mock
            void $init(Reader reader) throws IOException {
                throw new IOException("error");
            }
        };

        FileUtils.newBufferedReader(uncompressedFile);
    }

    @Test(expectedExceptions = IOException.class, expectedExceptionsMessageRegExp = "aaa")
    public void newBufferedReader_propagates_exception__002() throws IOException {
        new MockUp<BufferedReader>() {
            @Mock
            void $init(Reader reader) throws IOException {
                throw new IOException("aaa");
            }
        };
        new MockUp<FileInputStream>() {
            @Mock
            void close() throws IOException {
                throw new IOException("bbb");
            }
        };

        FileUtils.newBufferedReader(uncompressedFile);
    }

}
