package com.kyocoolcool.es.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chris
 * @version 1.0
 * @since 2020/11/15 9:50 AM
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private Integer age;
}
