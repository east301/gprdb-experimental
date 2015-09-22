package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import com.google.common.collect.Sets;
import jp.ac.tohoku.ecei.sb.gprdb.io.FileUtils;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLoader;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierMappingTypes;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierInfo;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierMapping;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class HomoloGeneIdentifierLoaderImpl implements IdentifierLoader {

    private final NcbiIdentifierLoaderConfig config;

    @Override
    public void load(@NotNull Consumer<RawIdentifierInfo> callback) throws IOException, ParseException {
        //
        String currentGroupId = null;
        Set<RawIdentifierMapping> currentGroup = Sets.newHashSet();

        Set<String> targetTaxonomyIds = this.config.getTargetTaxonomyIds();

        //
        try (BufferedReader reader = FileUtils.newBufferedReader(this.config.getPath())) {
            //
            String line;
            int index = 0;

            //
            while ((line = reader.readLine()) != null) {
                //
                index++;

                //
                String[] cols = line.trim().split("\t");
                if (cols.length != 6) {
                    throw new ParseException(this.config.getPath().getAbsolutePath(), index);
                }

                //
                if (currentGroupId != null && !currentGroupId.equals(cols[0])) {
                    emit(currentGroup, callback);
                    currentGroup.clear();
                }

                //
                currentGroupId = cols[0];
                if (!targetTaxonomyIds.contains(cols[1])) {
                    continue;
                }

                String mappingType = IdentifierMappingTypes.INTER_SPECIES;
                Optional<String> mappingHint = Optional.of("taxonomy:" + cols[1]);
                String geneId = "ncbigene:" + cols[2];

                currentGroup.add(new RawIdentifierMappingImpl(mappingType, mappingHint, geneId));
            }
        }

        //
        if (currentGroupId != null) {
            emit(currentGroup, callback);
        }
    }

    private void emit(@NotNull Set<RawIdentifierMapping> group, @NotNull Consumer<RawIdentifierInfo> callback) {
        for (RawIdentifierMapping source : group) {
            Set<RawIdentifierMapping> mappings = group.stream()
                .filter(m -> !m.getHint().get().equals(source.getHint().get()))
                .collect(Collectors.toSet());

            if (!mappings.isEmpty()) {
                callback.accept(new RawIdentifierInfoImpl(
                    source.getIdentifier(), Optional.empty(), Optional.empty(), mappings));
            }
        }
    }

    @Override
    public void close() {
        // do nothing
    }

}
