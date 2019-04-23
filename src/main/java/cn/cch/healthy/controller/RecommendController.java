package cn.cch.healthy.controller;

import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import cn.cch.healthy.util.AliasUtil;
import cn.cch.healthy.util.GetRemoveSetmeal;
import cn.cch.healthy.util.AdwardUtil;
import cn.cch.healthy.util.RecommendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class RecommendController {
    @Autowired
    UserinfoService userinfoService;
    @Autowired
    StandardIntakeService standardIntakeService;
    @Autowired
    SetmealInfomationService setmealInfomationService;
    @Autowired
    SetMealService setMealService;
    @Autowired
    PushInfomationService pushInfomationService;
    @Autowired
    RecipesService recipesService;
    @Autowired
    DietRecordService dietRecordService;
    @Autowired
    UserIllnessService userIllnessService;
    @Autowired
    RecommendUtil recommendUtil;



    /*
    * 描述：测试接口
    * */
    @ResponseBody
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public Map test(@RequestParam("userId") String userId) throws Exception {
        //List<SetmealInfomation> setMealList = setmealInfomationService.SelectByTime(2);

        return recommendUtil.recommend_score(Integer.parseInt(userId));
    }
    /*
    * 测试接口
    * */
    @ResponseBody
    @RequestMapping("/test2")
    public List<Map> test2() throws Exception {
        List<SetmealInfomation> setMealList = setmealInfomationService.SelectByTime(2);
        List<Adward> adwards = new ArrayList<Adward>();
        List list=new ArrayList();
        //设置奖品的名称(套餐id)和权重
        for(int i=0;i<setMealList.size();i++)
        {
            Adward adward = new Adward(setMealList.get(i).getSmId());
            adwards.add(adward);
            adward.setRecipeList(setMealService.selectRecipeId(setMealList.get(i).getSmId()));
        }
        AdwardUtil adwardUtil = new AdwardUtil();
        List<Map> records = dietRecordService.selectRecentRecord(1, 3, 2);
        adwards=adwardUtil.balanceWeight(records,adwards);
        return records;
    }
}
