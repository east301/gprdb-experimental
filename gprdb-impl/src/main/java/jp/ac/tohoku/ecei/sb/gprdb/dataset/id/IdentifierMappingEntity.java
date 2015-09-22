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

package jp.ac.tohoku.ecei.sb.gprdb.dataset.id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A JPA entity to store identifier mapping.
 *
 * @author Shu Tadaka
 */
@Entity(name = "IdentifierMapping")
@Table(name = "identifier_mapping", uniqueConstraints = @UniqueConstraint(columnNames = {"identifier_info_id", "type", "target_identifier"}))
@Indexed
@Data
@EqualsAndHashCode(of = {"identifierInfo", "type", "targetIdentifier"})
public class IdentifierMappingEntity {

    public static final String FIELD_TARGET_IDENTIFIER = "target_identifier";

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JoinColumn(name = "identifier_info_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IdentifierInfoEntity identifierInfo;

    @Column(name = "type", nullable = false, length = 255)
    private String type;

    @Column(name = "hint", length = 255)
    private String hint;

    @Column(name = FIELD_TARGET_IDENTIFIER, nullable = false, length = 255)
    @Field(name = FIELD_TARGET_IDENTIFIER, analyze = Analyze.YES, store = Store.NO)
    private String targetIdentifier;

}
