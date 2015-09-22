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

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

/**
 * An JPA entity to store identifier information.
 *
 * @author Shu Tadaka
 */
@Entity(name = "IdentifierInfo")
@Table(name = "identifier_info", uniqueConstraints = @UniqueConstraint(columnNames = {"dataset_id", "identifier"}))
@Indexed
@Data
@EqualsAndHashCode(of = {"datasetId", "identifier"})
public class IdentifierInfoEntity {

    public static final String FIELD_ID = "id";
    public static final String FIELD_IDENTIFIER = "identifier";
    public static final String FIELD_DATASET_ID = "dataset_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PRIORITY = "priority";
    public static final String FIELD_DESCRIPTION = "description";

    @Id
    @GeneratedValue
    @Column(name = FIELD_ID, nullable = false, unique = true)
    private Long id;

    @Column(name = FIELD_IDENTIFIER, nullable = false, length = 255)
    @Field(name = FIELD_IDENTIFIER, analyze = Analyze.YES, store = Store.NO)
    private String identifier;

    @Column(name = FIELD_DATASET_ID, nullable = false, length = 255)
    private String datasetId;

    @Column(name = FIELD_PRIORITY)
    private Integer priority;

    @Column(name = FIELD_NAME, length = 255)
    @Field(name = FIELD_NAME, analyze = Analyze.YES, store = Store.NO)
    private String name;

    @Column(name = FIELD_DESCRIPTION, length = 1024)
    @Field(name = FIELD_DESCRIPTION, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @OneToMany(mappedBy = "identifierInfo")
    private List<IdentifierMappingEntity> mappings = Lists.newArrayList();

}
