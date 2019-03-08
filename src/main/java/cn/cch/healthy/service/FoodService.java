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
}
