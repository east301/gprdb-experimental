package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.id;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLoader;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierInfo;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierMapping;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class HprdIdentifierLoaderImpl implements IdentifierLoader {

    private final SingleFileConfig config;

    @Override
    public void load(@NotNull Consumer<RawIdentifierInfo> callback) throws IOException, ParseException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(this.config.getPath().getAbsolutePath()))) {
            //
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                //
                index++;
                String[] cols = line.split("\t");
                if (cols.length != 8) {
                    throw new ParseException(this.config.getPath().getAbsolutePath(), index);
                }

                //
                String hprdId = "hprd:" + cols[0];
                String name = cols[7];

                String refseqNucleotideId = "refseq:" + cols[2];
                String refseqProteinId = "refseq:" + cols[3];
                String entrezGeneId = "ncbigene:" + cols[4];
                Collection<String> uniprotIds = Lists.transform(
                    Arrays.asList(cols[6].split(",")), id -> "uniprot:" + id);

                //
                List<RawIdentifierMapping> mappings = ImmutableList
                    .<String>builder()
                    .add(refseqNucleotideId)
                    .add(refseqProteinId)
                    .add(entrezGeneId)
                    .addAll(uniprotIds)
                    .build()
                    .stream()
                    .map(RawIdentifierMappingImpl::new)
                    .collect(Collectors.toList());

                //
                callback.accept(new RawIdentifierInfoImpl(hprdId, Optional.of(name), Optional.empty(), mappings));
            }
        }
    }

    @Override
    public void close() {
        // do nothing
    }

}
