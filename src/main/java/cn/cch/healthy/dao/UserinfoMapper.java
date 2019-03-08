package cn.cch.healthy.dao;

import cn.cch.healthy.model.Userinfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserinfoMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(Userinfo record);

    int insertSelective(Userinfo record);

    Userinfo selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(Userinfo record);

    int updateByPrimaryKey(Userinfo record);

    Userinfo selectByOpenid(String userOpenid);

    List selectAll();
}