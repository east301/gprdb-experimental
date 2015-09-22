/**
 * gprdb - a simple and flexible framework for gene-pair relation database construction
 * Copyright 2015 gprdb developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jp.ac.tohoku.ecei.sb.gprdb.dataset.relation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author Shu Tadaka
 */
@Component
public class StringToGenePairRelationTypeConverterImpl
    extends AbstractGenePairRelationConverter<String, GenePairRelationType> {

    @Autowired
    public StringToGenePairRelationTypeConverterImpl(@NotNull GenePairRelationManager manager) {
        super(manager);
    }

    @Override
    public GenePairRelationType convert(String source) {
        return this.manager.getType(source);
    }

}