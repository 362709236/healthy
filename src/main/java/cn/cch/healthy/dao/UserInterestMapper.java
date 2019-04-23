package cn.cch.healthy.dao;

import cn.cch.healthy.model.UserInterest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestMapper {
    int deleteByPrimaryKey(Integer uiId);

    int insert(UserInterest record);

    int insertSelective(UserInterest record);

    UserInterest selectByPrimaryKey(Integer uiId);

    List selectByUserId(int userId);

    int updateByPrimaryKeySelective(UserInterest record);

    int updateByPrimaryKey(UserInterest record);

    int deleteByuserId(int userId);

    int deleteByUserInterest(UserInterest record);
}