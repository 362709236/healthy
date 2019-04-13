package cn.cch.healthy.service;

import cn.cch.healthy.dao.DietRecordMapper;
import cn.cch.healthy.dao.DietRecord_SubMapper;
import cn.cch.healthy.model.DietRecord;
import cn.cch.healthy.model.DietRecord_Sub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
         //   System.out.println("第"+i+"次循环");
            Date date1 = records.get(i).getDrDate();
            Date date2 = new Date();
            long dayNum =  daysOfTwo_2(date1,date2);
            dayNum++;
           // System.out.println("和今天相差"+dayNum+"天");
            for (int j=0;j<list.size();j++)
            {
                Map map = new HashMap();
                map.put("recipeId",list.get(j));
                if(list.get(j)==44)
                    dayNum=-1;
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


    public long daysOfTwo_2(Date fDate,Date oDate) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        //跨年不会出现问题
        //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 0
        long days=(oDate.getTime()-fDate.getTime())/(1000*3600*24);
        return days;
    }

    public List SelectByDate(String dateStr,int userId){ return mapper.selectByDate(dateStr,userId); }

    public List SelectByDRid(int DR_id){ return subMapper.selectByDRid(DR_id); }

    public int insert(DietRecord DR){ return mapper.insert(DR); }

    public int insertsub(DietRecord_Sub DR_sub){ return subMapper.insert(DR_sub); }

    public List selectAll(){ return mapper.selectAll(); }
}
