package cn.cch.healthy.service;

import cn.cch.healthy.dao.InterestMapper;
import cn.cch.healthy.dao.UserInterestMapper;
import cn.cch.healthy.model.UserInterest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestService {
    @Autowired
    InterestMapper mapper;
    @Autowired
    UserInterestMapper userInterestMapper;
    /*
    * 描述：获得所有标签
    * */
    public List selectAllIntrest() {return mapper.selectAll(); }

    /*
    * 描述：获得一个套餐中与用户相匹配的标签的个数
    * */
    public int match(int smId,int userId){return 0;}

    /*
    * 查询用户是否有某个标签
    * */
    public boolean haveInterest(int interestId,int userId)
    {
        List<UserInterest> list = userInterestMapper.selectByUserId(userId);
        for (int i=0;i<list.size();i++)
        {
            if (list.get(i).getInterestId()==interestId)
                return true;
        }
        return false;
    }
}
