package com.example.waimai.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CanteenWindow implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer windowNumber;

    private Long canteenId;

    private Integer windowStatus;
}
