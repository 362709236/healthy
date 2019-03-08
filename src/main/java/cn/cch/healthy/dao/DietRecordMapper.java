package cn.cch.healthy.dao;

import cn.cch.healthy.model.DietRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface DietRecordMapper {
    int deleteByPrimaryKey(Integer drId);

    int insert(DietRecord record);

    int insertSelective(DietRecord record);

    DietRecord selectByPrimaryKey(Integer drId);

    int updateByPrimaryKeySelective(DietRecord record);

    int updateByPrimaryKey(DietRecord record);
}