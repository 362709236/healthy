package cn.cch.healthy.dao;

import cn.cch.healthy.model.SetMeal;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetMealMapper {
    int deleteByPrimaryKey(Integer smNumber);

    int insert(SetMeal record);

    int insertSelective(SetMeal record);

    SetMeal selectByPrimaryKey(Integer smNumber);


    int updateByPrimaryKeySelective(SetMeal record);

    int updateByPrimaryKey(SetMeal record);

    List selectBySMid(int id);

    List<Integer> selectRecipeId(Integer smId);

    int deleteByRecipesId(SetMeal record);

    SetMeal selectByRecipesid(int recipesId);
}