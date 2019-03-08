package cn.cch.healthy.service;

import cn.cch.healthy.dao.SetMealMapper;
import cn.cch.healthy.model.SetMeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetMealService {

    @Autowired
    SetMealMapper mapper;

    public int Insert(SetMeal SM){ return mapper.insert(SM); }

    public List SelectBySMid(int SM_id){ return mapper.selectBySMid(SM_id); }

    public int DeleteByRecipesId(SetMeal SM){ return mapper.deleteByRecipesId(SM); }

    public SetMeal SelectByRecipesid(int recipesId){ return mapper.selectByRecipesid(recipesId); }
}
