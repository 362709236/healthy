package cn.cch.healthy.dao;

import cn.cch.healthy.model.DietRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietRecordMapper {
    int deleteByPrimaryKey(Integer drId);

    int insert(DietRecord record);

    int insertSelective(DietRecord record);

    DietRecord selectByPrimaryKey(Integer drId);

    List<DietRecord> selectRecentDiet(@Param("userId")Integer userId,@Param("day")Integer day,@Param("time")Integer time);

    int updateByPrimaryKeySelective(DietRecord record);

    int updateByPrimaryKey(DietRecord record);

    List selectByDate(@Param("dateStr") String dateStr,@Param("userId") int userId);
}