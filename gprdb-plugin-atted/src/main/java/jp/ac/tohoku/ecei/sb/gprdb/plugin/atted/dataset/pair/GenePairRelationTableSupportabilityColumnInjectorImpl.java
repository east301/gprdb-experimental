package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.pair;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.Identifier;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationTable;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationTableCustomColumnInjector;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.GenePairRelationTableCustomColumnPosition;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStore;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStoreManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * An implementation of {@link GenePairRelationTableCustomColumnInjector}
 * to add "supportability" column.
 *
 * @author Shu Tadaka
 */
@Component
@ConditionalOnWebApplication
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class GenePairRelationTableSupportabilityColumnInjectorImpl implements GenePairRelationTableCustomColumnInjector {

    private static final String[] STARTS = {"", "☆", "☆☆", "☆☆☆"};

    private final KeyValueStore<String, Integer> supportabilityMap;

    @Autowired
    public GenePairRelationTableSupportabilityColumnInjectorImpl(@NotNull KeyValueStoreManager manager) {
        this.supportabilityMap = manager.getMap("atted-supportability", String.class, Integer.class);
    }

    @Override
    public String getName() {
        return "Supportability";
    }

    @Override
    public GenePairRelationTableCustomColumnPosition getPosition() {
        return GenePairRelationTableCustomColumnPosition.AFTER_DESCRIPTION;
    }

    @Override
    public void prepare(@NotNull GenePairRelationTable table) {
        // do nothing
    }

    @Override
    public String getCellAsHtml(@NotNull Identifier interactingGene) {
        Integer value = this.supportabilityMap.get(interactingGene.getId());
        return value != null ? STARTS[value] : "-";
    }

    @Override
    public void close() {
        // do nothing
    }

}
