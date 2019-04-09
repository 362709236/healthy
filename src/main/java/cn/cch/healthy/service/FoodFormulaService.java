package cn.cch.healthy.service;

import cn.cch.healthy.dao.FoodFormulaMapper;
import cn.cch.healthy.model.Food;
import cn.cch.healthy.model.FoodFormula;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodFormulaService {

    @Autowired
    FoodFormulaMapper mapper;

    public List SelectByRecipesId(int recipesId){ return mapper.selectByrecipesId(recipesId); }

    public List SelectByFoodid(int food_id){ return mapper.selectByFoodid(food_id); }

    public int DeleteByPrimaryKey(int id){ return mapper.deleteByPrimaryKey(id); }

    public int UpdateByPrimaryKeySelective(FoodFormula FF){ return mapper.updateByPrimaryKeySelective(FF); }

    public int DeleteByRecipesId(int recipesId){ return mapper.deleteByRecipesId(recipesId); }

    public int insert(FoodFormula FF){ return mapper.insert(FF); }
}
