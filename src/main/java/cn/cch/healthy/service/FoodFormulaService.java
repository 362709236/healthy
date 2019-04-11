package cn.cch.healthy.service;

import cn.cch.healthy.dao.FoodFormulaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodFormulaService {

    @Autowired
    FoodFormulaMapper mapper;

    public List SelectByRecipesId(int recipesId){ return mapper.selectByrecipesId(recipesId); }

    public List SelectByFoodid(int food_id){ return mapper.selectByFoodid(food_id); }

    public List selectRecipeIdByFoodid(int food_id){return mapper.selectRecipeIdByFoodid(food_id);}
}
