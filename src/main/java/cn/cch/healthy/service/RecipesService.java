package cn.cch.healthy.service;

import cn.cch.healthy.dao.RecipesMapper;
import cn.cch.healthy.model.Recipes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
public class RecipesService {

    @Autowired
    RecipesMapper mapper;

    public List Findall(){ return mapper.findall(); }

    public int UpdateByPrimaryKeySelective(Recipes recipes){ return mapper.updateByPrimaryKeySelective(recipes); }

    public Recipes SelectByPrimaryKey(int Recipes_id){ return mapper.selectByPrimaryKey(Recipes_id); }

    public String getName(int Recipes_id) { return mapper.getName(Recipes_id); }
}
