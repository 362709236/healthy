package cn.cch.healthy.dao;

import cn.cch.healthy.model.Interest;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestMapper {
    int deleteByPrimaryKey(Integer interestId);

    int insert(Interest record);

    int insertSelective(Interest record);

    Interest selectByPrimaryKey(Integer interestId);

    int updateByPrimaryKeySelective(Interest record);

    int updateByPrimaryKey(Interest record);
}