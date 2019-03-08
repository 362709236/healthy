package cn.cch.healthy.service;

import cn.cch.healthy.dao.PushInfomationMapper;
import cn.cch.healthy.model.PushInfomation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushInfomationService {
    @Autowired
    PushInfomationMapper mapper;

    public List<PushInfomation> selectRecentPush(int day){return mapper.selectRecentPush(day);}
}
