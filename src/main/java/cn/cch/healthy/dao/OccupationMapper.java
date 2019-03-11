package cn.cch.healthy.dao;

import cn.cch.healthy.model.Occupation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccupationMapper {
    int deleteByPrimaryKey(Integer occupationId);

    int insert(Occupation record);

    int insertSelective(Occupation record);

    Occupation selectByPrimaryKey(Integer occupationId);

    int updateByPrimaryKeySelective(Occupation record);

    int updateByPrimaryKey(Occupation record);

    int getPressure(String occupation);

    List selectAll();
}