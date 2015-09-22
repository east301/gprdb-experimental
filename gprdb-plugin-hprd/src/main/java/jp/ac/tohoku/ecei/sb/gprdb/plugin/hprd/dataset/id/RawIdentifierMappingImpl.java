package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierMappingTypes;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierMapping;
import lombok.Value;

import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Value
class RawIdentifierMappingImpl implements RawIdentifierMapping {

    private static final long serialVersionUID = 1L;

    private final String type = IdentifierMappingTypes.INTRA_SPECIES;
    private final Optional<String> hint = Optional.empty();
    private final String identifier;

}
