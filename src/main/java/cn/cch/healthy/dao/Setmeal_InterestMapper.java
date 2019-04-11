package cn.cch.healthy.dao;

import cn.cch.healthy.model.Setmeal_Interest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Setmeal_InterestMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Setmeal_Interest record);

    int insertSelective(Setmeal_Interest record);

    Setmeal_Interest selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Setmeal_Interest record);

    int updateByPrimaryKey(Setmeal_Interest record);

    List selectBySmId(int smId);
}