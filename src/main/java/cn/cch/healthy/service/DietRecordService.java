package cn.cch.healthy.service;

import cn.cch.healthy.dao.DietRecordMapper;
import cn.cch.healthy.dao.DietRecord_SubMapper;
import cn.cch.healthy.model.DietRecord;
import cn.cch.healthy.model.DietRecord_Sub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DietRecordService {
    @Autowired
    DietRecordMapper mapper;
    @Autowired
    DietRecord_SubMapper subMapper;

    public List<DietRecord> selectRecentRecord(int userId,int day)
    {
        List<DietRecord> records = mapper.selectRecentDiet(userId,day);
        List<DietRecord_Sub> subRecords = new ArrayList<DietRecord_Sub>();
        for(int i=0;i<records.size();i++)
        {
            List<DietRecord_Sub> list = subMapper.selectByDRid(records.get(i).getDrId());
            subRecords.addAll(list);
        }
    }
}
