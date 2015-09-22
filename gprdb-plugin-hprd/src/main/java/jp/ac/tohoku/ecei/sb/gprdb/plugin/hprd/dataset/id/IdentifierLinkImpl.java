package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLink;
import lombok.Value;

import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Value
class IdentifierLinkImpl implements IdentifierLink {

    private static final long serialVersionUID = 1L;

    private final String url;
    private final Optional<String> logoUrl = Optional.empty();

}
