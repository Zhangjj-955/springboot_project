package com.example.waimai.dto;

import com.example.waimai.entity.Setmeal;
import com.example.waimai.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
