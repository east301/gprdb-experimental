package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.id.IdentifierLink;
import lombok.Value;

import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Value
class NcbiIdentifierLinkImpl implements IdentifierLink {

    private final String url;
    private final Optional<String> logoUrl = Optional.of("/static/gprdb-plugin-ncbi/ncbi-logo-32px.png");

}
