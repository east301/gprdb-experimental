package jp.ac.tohoku.ecei.sb.gprdb.plugin.ncbi.dataset.id;

import com.google.common.collect.Sets;
import jp.ac.tohoku.ecei.sb.gprdb.io.SingleFileConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author Shu Tadaka
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NcbiIdentifierLoaderConfig extends SingleFileConfig {

    private Set<String> targetTaxonomyIds = Sets.newHashSet();

}
