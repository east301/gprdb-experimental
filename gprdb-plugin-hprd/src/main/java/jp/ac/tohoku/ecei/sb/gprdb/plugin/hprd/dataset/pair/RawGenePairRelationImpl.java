package jp.ac.tohoku.ecei.sb.gprdb.plugin.hprd.dataset.pair;

import jp.ac.tohoku.ecei.sb.gprdb.dataset.relation.RawGenePairRelation;
import lombok.Value;

import java.util.Map;

/**
 * @author Shu Tadaka
 */
@Value
class RawGenePairRelationImpl implements RawGenePairRelation {

    private final String guideGene;
    private final String relationType = "ppi";
    private final Map<String, Double> relationStrengthMap;

}
