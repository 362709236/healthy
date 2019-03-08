package cn.cch.healthy.dao;

import cn.cch.healthy.model.StandardIntake_Female;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardIntake_FemaleMapper {
    int deleteByPrimaryKey(Integer siId);

    int insert(StandardIntake_Female record);

    int insertSelective(StandardIntake_Female record);

    StandardIntake_Female selectByPrimaryKey(Integer siId);

    int updateByPrimaryKeySelective(StandardIntake_Female record);

    int updateByPrimaryKey(StandardIntake_Female record);
}