package cn.cch.healthy.dao;

import cn.cch.healthy.model.UserInterest;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterestMapper {
    int deleteByPrimaryKey(Integer uiId);

    int insert(UserInterest record);

    int insertSelective(UserInterest record);

    UserInterest selectByPrimaryKey(Integer uiId);

    int updateByPrimaryKeySelective(UserInterest record);

    int updateByPrimaryKey(UserInterest record);
}