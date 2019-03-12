package cn.cch.healthy.service;

import cn.cch.healthy.dao.DietRecordMapper;
import cn.cch.healthy.dao.DietRecord_SubMapper;
import cn.cch.healthy.model.DietRecord;
import cn.cch.healthy.model.DietRecord_Sub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DietRecordService {
    @Autowired
    DietRecordMapper mapper;
    @Autowired
    DietRecord_SubMapper subMapper;

    public List<DietRecord> selectRecentRecord(int userId,int day,int time)
    {
        List<DietRecord> records = mapper.selectRecentDiet(userId,day,time);
        return records;
        /*if(records.size()==0)
            return null;
        List<Map> recordMap = new ArrayList<Map>();
        for(int i=0;i<records.size();i++)
        {
            Map map = new HashMap();
            List<Integer> list = subMapper.selectByDRid(records.get(i).getDrId());
            map.put("recipeIds",list);
            if ()
        }*/

    }
}
