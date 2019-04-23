package cn.cch.healthy.service;

import cn.cch.healthy.dao.RecipesMapper;
import cn.cch.healthy.model.Recipes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipesService {

    @Autowired
    RecipesMapper mapper;

    public List Findall(){ return mapper.findall(); }

    public int UpdateByPrimaryKeySelective(Recipes recipes){ return mapper.updateByPrimaryKeySelective(recipes); }

    public Recipes SelectByPrimaryKey(int Recipes_id){ return mapper.selectByPrimaryKey(Recipes_id); }

    public String getName(int Recipes_id) { return mapper.getName(Recipes_id); }

    public int insert(Recipes recipes){ return mapper.insert(recipes); }

    public int DeleteByPrimaryKey(int id){ return mapper.deleteByPrimaryKey(id); }

    public int GetId(String recipesName){ return mapper.getId(recipesName); }

    public List VagueSelectRecipes(String recipesName){ return mapper.vagueSelectRecipes(recipesName); }

    public Recipes SelectByName(String recipesName){ return mapper.selectByName(recipesName); }

    public List<String> SelectByType(String recipesType){ return mapper.selectByType(recipesType); }
}
