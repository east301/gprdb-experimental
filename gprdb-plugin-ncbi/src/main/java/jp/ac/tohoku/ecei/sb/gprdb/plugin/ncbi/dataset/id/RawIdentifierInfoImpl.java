package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierInfo;
import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.RawIdentifierMapping;
import lombok.Value;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Value
class RawIdentifierInfoImpl implements RawIdentifierInfo {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final Optional<String> name;
    private final Optional<String> description;
    private final Collection<RawIdentifierMapping> mappings;

}
