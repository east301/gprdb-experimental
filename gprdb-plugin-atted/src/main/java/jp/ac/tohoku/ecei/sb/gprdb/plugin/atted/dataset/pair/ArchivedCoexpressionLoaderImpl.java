package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationLoader;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.RawGenePairRelation;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class ArchivedCoexpressionLoaderImpl implements GenePairRelationLoader {

    private final SingleFileConfig config;

    @Override
    public void loadGenes(@NotNull Consumer<String> callback) throws IOException, ParseException {
        try (ArchiveFile archive = openArchive()) {
            //
            ArchiveEntry entry = null;
            while ((entry = archive.getNextEntry()) != null) {
                if (extractGuideGeneId(entry) != null) {
                    break;
                }
            }

            if (entry == null) {
                String path = this.config.getPath().getAbsolutePath();
                throw new IllegalStateException("Invalid file: " + path);
            }

            //
            Map<String, Double> mrMap = Maps.newHashMap();
            Map<String, Double> pccMap = Maps.newHashMap();
            parseArchiveEntry(archive, entry, mrMap, pccMap);

            //
            String guideGeneId = extractGuideGeneId(entry);
            callback.accept(guideGeneId);
            Sets.difference(mrMap.keySet(), ImmutableSet.of(guideGeneId)).forEach(callback);
        }
    }

    @Override
    public void loadRelations(@NotNull Consumer<RawGenePairRelation> callback)
        throws IOException, ParseException {

        try (ArchiveFile archive = openArchive()) {
            ArchiveEntry entry;
            while ((entry = archive.getNextEntry()) != null) {
                //
                String guideGeneId = extractGuideGeneId(entry);
                if (guideGeneId == null) {
                    continue;
                }

                //
                Map<String, Double> mrMap = Maps.newHashMap();
                Map<String, Double> pccMap = Maps.newHashMap();
                parseArchiveEntry(archive, entry, mrMap, pccMap);

                callback.accept(new RawGenePairRelationImpl(guideGeneId, "mr", mrMap));
                callback.accept(new RawGenePairRelationImpl(guideGeneId, "pcc", pccMap));
            }
        }
    }

    private ArchiveFile openArchive() throws IOException {
        //
        File source = this.config.getPath();
        String name = this.config.getPath().getName();

        if (name.endsWith(".tar.bz2") || name.endsWith(".tbz2")) {
            return new ArchiveFile.ArchiveInputStreamWrapper(
                new TarArchiveInputStream(new BZip2CompressorInputStream(new FileInputStream(source))));
        } else if (name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
            return new ArchiveFile.ArchiveInputStreamWrapper(
                new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(source))));
        } else if (name.endsWith(".zip")) {
            return new ArchiveFile.ArchiveInputStreamWrapper(
                new ZipArchiveInputStream(new FileInputStream(source)));
        } else if (name.endsWith(".7z")) {
            return new ArchiveFile.SevenZipFileWrapper(
                new SevenZFile(source));
        }

        throw new IllegalStateException("cannot detect compression format from file extension: " + name);
    }

    private static String extractGuideGeneId(@NotNull ArchiveEntry entry) {
        //
        if (entry.isDirectory()) {
            return null;
        }

        //
        String[] cols = entry.getName().split("/");
        String geneId = cols[cols.length - 1];

        try {
            Integer.parseInt(geneId);
            return "ncbigene:" + geneId;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void parseArchiveEntry(
        @NotNull ArchiveFile stream,
        @NotNull ArchiveEntry entry,
        @NotNull Map<String, Double> mrMap,
        @NotNull Map<String, Double> pccMap)
        throws IOException, ParseException {

        // BufferedReader must not be closed.
        // Invocation of BufferedReader#close also closes ArchiveInputStream.
        ByteArrayInputStream buffer = new ByteArrayInputStream(stream.getCurrentEntryData());
        BufferedReader reader = new BufferedReader(new InputStreamReader(buffer));
        String line;
        int index = 0;

        //
        while ((line = reader.readLine()) != null) {
            //
            index++;

            //
            String[] cols = line.trim().split("\t");
            if (cols.length != 3) {
                throw new ParseException(
                    this.config.getPath().getAbsolutePath() + ":" + entry.getName(), index);
            }

            //
            String interactingGene = "ncbigene:" + cols[0];
            mrMap.put(interactingGene, Double.parseDouble(cols[1]));
            pccMap.put(interactingGene, Double.parseDouble(cols[2]));
        }
    }

    @Override
    public void close() {
        // do nothing
    }

}
