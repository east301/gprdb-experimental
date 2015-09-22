package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierMapping;
import lombok.Value;

import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Value
class RawIdentifierMappingImpl implements RawIdentifierMapping {

    private static final long serialVersionUID = 1L;

    private final String type;
    private final Optional<String> hint;
    private final String identifier;

}
