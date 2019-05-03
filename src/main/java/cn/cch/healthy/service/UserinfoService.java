package cn.cch.healthy.service;

import cn.cch.healthy.dao.UserinfoMapper;
import cn.cch.healthy.model.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserinfoService {

    @Autowired
    UserinfoMapper mapper;

    public int insert(Userinfo user){ return mapper.insert(user); }

    public Userinfo SelectByOpenid(String openid){ return mapper.selectByOpenid(openid); }

    public int UpdateByPrimaryKeySelective(Userinfo user){ return mapper.updateByPrimaryKeySelective(user); }

    public List SelectAll(){ return mapper.selectAll(); }

    public Userinfo selectByPrimarykey(int userId){return mapper.selectByPrimaryKey(userId);}

    public int updateByPrimaryKey(Userinfo user){ return mapper.updateByPrimaryKey(user); }
}
