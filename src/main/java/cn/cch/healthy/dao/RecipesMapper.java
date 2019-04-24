package cn.cch.healthy.dao;

import cn.cch.healthy.model.Recipes;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipesMapper {
    int deleteByPrimaryKey(Integer recipesId);

    int insert(Recipes record);

    int insertSelective(Recipes record);

    Recipes selectByPrimaryKey(Integer recipesId);

    int updateByPrimaryKeySelective(Recipes record);

    int updateByPrimaryKey(Recipes record);
	
	List findall();

    String getName(Integer recipesId);

    int getId(String recipesName);

    List vagueSelectRecipes(String recipesName);

    Recipes selectByName(String recipesName);

    List selectByType(@Param("recipesType") String recipesType);

    List selectRecipesByType(@Param("recipesType") String recipesType);

    List<Recipes> getRecipesByType(String typeTag);

    String getTypeByid(Integer recipesId);
}