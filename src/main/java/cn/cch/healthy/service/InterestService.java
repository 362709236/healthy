package cn.cch.healthy.service;

import cn.cch.healthy.dao.InterestMapper;
import cn.cch.healthy.dao.Setmeal_InterestMapper;
import cn.cch.healthy.dao.UserInterestMapper;
import cn.cch.healthy.model.Interest;
import cn.cch.healthy.model.Setmeal_Interest;
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
    @Autowired
    Setmeal_InterestMapper setmeal_interestMapper;
    /*
    * 描述：获得所有标签
    * */
    public List selectAllIntrest() {return mapper.selectAll(); }

    /*
    * 描述：获得一个套餐中与用户相匹配的标签的个数
    * */
    public int match(int smId,int userId)
    {
        List<Integer> user = userInterestMapper.selectByUserId(userId);
        List<Integer> setMeal = setmeal_interestMapper.selectBySmId(smId);
        int num=0;
        for(int i=0;i<user.size();i++)
            for (int j=0;j<setMeal.size();j++)
                if (user.get(i)==setMeal.get(j))
                    num++;
        return num;
    }

    /*
    * 查询用户是否有某个标签
    * */
    public boolean haveInterest(int interestId,int userId)
    {
        List<Integer> list = userInterestMapper.selectByUserId(userId);
        for (int i=0;i<list.size();i++)
        {
            if (list.get(i)==interestId)
                return true;
        }
        return false;
    }

    public int GetID(String name){ return mapper.getID(name); }

    public int insertuserInterest(UserInterest UI){ return userInterestMapper.insert(UI); }

    public int UpdateuserInterest(UserInterest UI){ return userInterestMapper.updateByPrimaryKeySelective(UI); }

    public List<Integer> SelectByUserId(int userId){ return userInterestMapper.selectByUserId(userId); }

    public int DeleteByuserId(int userId){ return userInterestMapper.deleteByuserId(userId); }

    public int DeleteByUserInterest(UserInterest UI){ return  userInterestMapper.deleteByUserInterest(UI); }

    public Interest SelectByPrimaryKey(int id){ return mapper.selectByPrimaryKey(id); }

    public int insertSetmealIntest(Setmeal_Interest setmeal_interest){return setmeal_interestMapper.insert(setmeal_interest);}
}
