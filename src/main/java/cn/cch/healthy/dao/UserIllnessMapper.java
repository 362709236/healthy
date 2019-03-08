package cn.cch.healthy.dao;

import cn.cch.healthy.model.UserIllness;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserIllnessMapper {
    int deleteByPrimaryKey(Integer uinId);

    int insert(UserIllness record);

    int insertSelective(UserIllness record);

    UserIllness selectByPrimaryKey(Integer uinId);

    int updateByPrimaryKeySelective(UserIllness record);

    int updateByPrimaryKey(UserIllness record);

    List selectByUserid(int userId);

    int deleteByUserid(int userId);

    int deleteByUserIll(UserIllness record);
}