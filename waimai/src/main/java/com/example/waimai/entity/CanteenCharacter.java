package com.example.waimai.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CanteenCharacter implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long canteenId;
    private String canteenCharacter;
}
