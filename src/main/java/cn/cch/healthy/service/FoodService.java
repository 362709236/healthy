package cn.cch.healthy.service;

import cn.cch.healthy.dao.FoodMapper;
import cn.cch.healthy.model.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodService {

    @Autowired
    FoodMapper mapper;

    public Food selectByPrimaryKey(int Food_id){ return mapper.selectByPrimaryKey(Food_id); }

    public int GetId(String foodname){ return mapper.getId(foodname); }

    public Food SelectByName(String foodName){ return mapper.selectByName(foodName); }

    public int insert(Food food){ return mapper.insert(food); }

    public int UpdateByPrimaryKeySelective(Food food){ return mapper.updateByPrimaryKeySelective(food); }

    public int DeleteByPrimaryKey(int id){ return mapper.deleteByPrimaryKey(id);}
}
