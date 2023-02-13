package com.example.waimai.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Canteen implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Integer status;

    private Integer number;
}
