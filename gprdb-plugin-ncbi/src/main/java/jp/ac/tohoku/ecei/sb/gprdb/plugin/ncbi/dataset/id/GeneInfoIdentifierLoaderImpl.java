package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import jp.ac.tohoku.ecei.sb.gprdb.io.FileUtils;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLoader;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierMappingTypes;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierInfo;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierMapping;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class GeneInfoIdentifierLoaderImpl implements IdentifierLoader {

    private static final Map<String, String> PREFIX_MAP = ImmutableMap.<String, String>builder()
        .put("Ensembl", "ensembl")
        .put("FLYBASE", "flybase")
        .put("HPRD", "hprd")
        .put("SGD", "sgd")
        .put("TAIR", "tair.locus")
        .put("miRBase", "mirbase")
        .build();

    private final NcbiIdentifierLoaderConfig config;

    @Override
    public void load(@NotNull Consumer<RawIdentifierInfo> callback) throws IOException, ParseException {
        //
        Set<String> targetTaxonomyIds = this.config.getTargetTaxonomyIds();

        //
        try (BufferedReader reader = FileUtils.newBufferedReader(this.config.getPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                //
                String[] cols = line.trim().split("\t");
                if (cols.length == 0 || isHeader(cols) || !isTargetSpecies(cols, targetTaxonomyIds)) {
                    continue;
                }

                //
                String geneId = "ncbigene:" + cols[1];
                String geneSymbol = "-".equals(cols[2]) ? null : cols[2];
                String description = "-".equals(cols[8]) ? null : cols[8];
                Collection<RawIdentifierMapping> crossReferences = parseCrossReferences(cols[5]);

                if (geneSymbol != null || description != null || !crossReferences.isEmpty()) {
                    callback.accept(new RawIdentifierInfoImpl(
                        geneId,
                        Optional.ofNullable(geneSymbol),
                        Optional.ofNullable(description),
                        crossReferences));
                }
            }
        }
    }

    private static boolean isHeader(@NotEmpty String[] cols) {
        return cols[0].startsWith("#");
    }

    private static boolean isTargetSpecies(@NotEmpty String[] cols, @NotNull Set<String> targetTaxonomyIds) {
        return targetTaxonomyIds.isEmpty() || targetTaxonomyIds.contains(cols[0]);
    }

    private static Collection<RawIdentifierMapping> parseCrossReferences(@NotNull String source) {
        //
        if ("-".equals(source)) {
            return Collections.emptyList();
        }

        //
        List<RawIdentifierMapping> result = Lists.newArrayList();
        for (String pairString : source.split("\\|")) {
            String[] pairArray = pairString.split(":", 2);

            String prefix = PREFIX_MAP.get(pairArray[0]);
            if (prefix != null) {
                String id = prefix + ":" + pairArray[1];
                result.add(
                    new RawIdentifierMappingImpl(
                        IdentifierMappingTypes.INTRA_SPECIES, Optional.empty(), id));
            }
        }

        return result;
    }

    @Override
    public void close() {
        // do nothing
    }

}
