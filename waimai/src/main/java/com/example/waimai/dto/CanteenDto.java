package com.example.waimai.dto;

import com.example.waimai.entity.Canteen;
import com.example.waimai.entity.CanteenCharacter;
import lombok.Data;

import java.util.List;

@Data
public class CanteenDto extends Canteen {
    List<String> characterStringList;
}
