package cn.cch.healthy.service;

import cn.cch.healthy.dao.DietRecordMapper;
import cn.cch.healthy.dao.DietRecord_SubMapper;
import cn.cch.healthy.model.DietRecord;
import cn.cch.healthy.model.DietRecord_Sub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DietRecordService {
    @Autowired
    DietRecordMapper mapper;
    @Autowired
    DietRecord_SubMapper subMapper;
    //刚吃完一天的菜损失的权重
    @Value("${deWeight.one}")
    double oneDeWeight;
    //刚吃完两天的菜损失的权重
    @Value("${deWeight.two}")
    double twoDeWeight;
    //刚吃完三天的菜损失的权重
    @Value("${deWeight.three}")
    double threeDeWeight;

    public List<Map> selectRecentRecord(int userId,int day,int time) throws Exception {
        List<Map> recordMap = new ArrayList<Map>();
        List<DietRecord> records = mapper.selectRecentDiet(userId,day,time);
        if (records.size()==0)
            return recordMap;
        System.out.println("总共有"+records.size()+"条记录");
        if(records.size()==0)
            return null;
        for(int i=0;i<records.size();i++)
        {
            List<Integer> list = subMapper.selectByDRid(records.get(i).getDrId());
            System.out.println("第"+i+"次循环");
            Date date1 = records.get(i).getDrDate();
            Date date2 = new Date();
            long dayNum =  getInterval(date1,date2);
            for (int j=0;j<list.size();j++)
            {
                Map map = new HashMap();
                map.put("recipeId",list.get(j));
                if(list.get(j)==44)
                    dayNum=0;
                if(dayNum==1)
                    map.put("deWeight",oneDeWeight);
                else if (dayNum==2)
                    map.put("deWeight",twoDeWeight);
                else if (dayNum==3)
                    map.put("deWeight",threeDeWeight);
                else
                    map.put("deWeight",0);
                recordMap.add(map);
            }
        }
        return recordMap;
    }

    public long getInterval(Date begin_date, Date end_date) throws Exception{
        long day = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if(begin_date != null){
            String begin = sdf.format(begin_date);
            begin_date  = sdf.parse(begin);
        }
        if(end_date!= null){
            String end= sdf.format(end_date);
            end_date= sdf.parse(end);
        }
        day = (end_date.getTime()-begin_date.getTime())/(24*60*60*1000);
        return day;
    }
}
