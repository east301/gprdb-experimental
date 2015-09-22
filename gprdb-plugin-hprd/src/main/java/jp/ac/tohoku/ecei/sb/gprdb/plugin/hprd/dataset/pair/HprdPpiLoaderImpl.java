package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationLoader;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.RawGenePairRelation;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Shu Tadaka
 */
@RequiredArgsConstructor
class HprdPpiLoaderImpl implements GenePairRelationLoader {

    private final SingleFileConfig config;

    @Override
    public void loadGenes(@NotNull Consumer<String> callback) throws IOException, ParseException {
        try (BufferedReader reader = newReader()) {
            String line;
            int index = 0;
            Set<String> hprdIds = Sets.newHashSet();

            while ((line = reader.readLine()) != null) {
                //
                index++;

                String[] cols = line.trim().split("\t");
                if (cols.length != 8) {
                    throw new ParseException(this.config.getPath().getAbsolutePath(), index);
                }

                //
                hprdIds.add("hprd:" + cols[1]);
                hprdIds.add("hprd:" + cols[4]);
            }

            hprdIds.forEach(callback);
        }
    }

    @Override
    public void loadRelations(@NotNull Consumer<RawGenePairRelation> callback) throws IOException, ParseException {
        //
        Multimap<String, String> relationMap = ArrayListMultimap.create();

        //
        try (BufferedReader reader = newReader()) {
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                //
                index++;

                String[] cols = line.trim().split("\t");
                if (cols.length != 8) {
                    throw new ParseException(this.config.getPath().getAbsolutePath(), index);
                }

                //
                String hprdId1 = "hprd:" + cols[1];
                String hprdId2 = "hprd:" + cols[4];

                relationMap.put(hprdId1, hprdId2);
                if (!hprdId1.equals(hprdId2)) {
                    relationMap.put(hprdId2, hprdId1);
                }
            }
        }

        //
        for (String hprdId1 : relationMap.keySet()) {
            callback.accept(new RawGenePairRelationImpl(hprdId1, Maps.toMap(relationMap.get(hprdId1), id -> 1.0d)));
        }
    }

    private BufferedReader newReader() throws IOException {
        return Files.newBufferedReader(Paths.get(this.config.getPath().getAbsolutePath()));
    }

    @Override
    public void close() {
        // do nothing
    }

}
