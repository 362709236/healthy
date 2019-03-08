package cn.cch.healthy.dao;

import cn.cch.healthy.model.SetmealInfomation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetmealInfomationMapper {
    int deleteByPrimaryKey(Integer smId);

    int insert(SetmealInfomation record);

    int insertSelective(SetmealInfomation record);

    SetmealInfomation selectByPrimaryKey(Integer smId);

    int updateByPrimaryKeySelective(SetmealInfomation record);

    int updateByPrimaryKey(SetmealInfomation record);

    List selectByTime(int time);
}