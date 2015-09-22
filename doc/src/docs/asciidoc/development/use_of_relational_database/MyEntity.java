package com.example.my.pkg;

import lombok.Data;
import javax.persistence.Entity;

@Entity
@Data
public class MyEntity {
    private Long id;
    private String value;
}
