package cn.cch.healthy.dao;

import cn.cch.healthy.model.StandardIntake_Male;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardIntake_MaleMapper {
    int deleteByPrimaryKey(Integer siId);

    int insert(StandardIntake_Male record);

    int insertSelective(StandardIntake_Male record);

    StandardIntake_Male selectByPrimaryKey(Integer siId);

    int updateByPrimaryKeySelective(StandardIntake_Male record);

    int updateByPrimaryKey(StandardIntake_Male record);
}