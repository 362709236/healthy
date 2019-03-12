package cn.cch.healthy.dao;

import cn.cch.healthy.model.DietRecord_Sub;
import cn.cch.healthy.model.Interest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietRecord_SubMapper {
    int deleteByPrimaryKey(Integer drsId);

    int insert(DietRecord_Sub record);

    int insertSelective(DietRecord_Sub record);

    DietRecord_Sub selectByPrimaryKey(Integer drsId);

    int updateByPrimaryKeySelective(DietRecord_Sub record);

    int updateByPrimaryKey(DietRecord_Sub record);

    List<Integer> selectByDRid(Integer drId);
}