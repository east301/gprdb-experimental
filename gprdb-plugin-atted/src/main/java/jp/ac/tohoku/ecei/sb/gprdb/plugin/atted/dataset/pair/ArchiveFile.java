package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.pair;

import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Shu Tadaka
 */
interface ArchiveFile extends Closeable {

    ArchiveEntry getNextEntry() throws IOException;

    byte[] getCurrentEntryData() throws IOException;

    @RequiredArgsConstructor
    class ArchiveInputStreamWrapper implements ArchiveFile {

        private final ArchiveInputStream stream;
        private ArchiveEntry entry = null;

        @Override
        public ArchiveEntry getNextEntry() throws IOException {
            this.entry = this.stream.getNextEntry();
            return this.entry;
        }

        @Override
        public byte[] getCurrentEntryData() throws IOException {
            byte[] buffer = new byte[(int)this.entry.getSize()];
            this.stream.read(buffer);
            return buffer;
        }

        @Override
        public void close() throws IOException {
            this.stream.close();
        }

    }

    @RequiredArgsConstructor
    class SevenZipFileWrapper implements ArchiveFile {

        private final SevenZFile file;
        private ArchiveEntry entry = null;

        @Override
        public ArchiveEntry getNextEntry() throws IOException {
            this.entry = this.file.getNextEntry();
            return this.entry;
        }

        @Override
        public byte[] getCurrentEntryData() throws IOException {
            byte[] buffer = new byte[(int)this.entry.getSize()];
            this.file.read(buffer);
            return buffer;
        }

        @Override
        public void close() throws IOException {
            this.file.close();
        }

    }

}
