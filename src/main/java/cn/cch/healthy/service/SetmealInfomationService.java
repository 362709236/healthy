package cn.cch.healthy.service;

import cn.cch.healthy.dao.SetmealInfomationMapper;
import cn.cch.healthy.model.SetmealInfomation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealInfomationService {

    @Autowired
    SetmealInfomationMapper mapper;

    public int insert(SetmealInfomation SmI){ return mapper.insert(SmI);}

    public List SelectByTime(int time){return mapper.selectByTime(time);}

    public int UpdateByPrimaryKeySelective(SetmealInfomation SmI){ return mapper.updateByPrimaryKeySelective(SmI); }

}
