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
import org.springframework.ui.Model;
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
    @RequestMapping(value = "/test2",method = RequestMethod.POST)
    public String test2(@RequestParam("userId") String userId, Model model) throws Exception {
        List<List> setmealList = (List<List>) recommendUtil.recommend_score(Integer.parseInt(userId),30).get("recipes");
        model.addAttribute("setmeals",setmealList);
        return "data_launcher";
    }
    /*
    * 测试接口
    * */
    @ResponseBody
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public Map test(@RequestParam("userId") String userId
            ,@RequestParam("num") String num) throws Exception {
    //    Map result = recommendUtil.recommend_score(Integer.parseInt(userId));
        int hour = Calendar.HOUR;
        double rate=0;
        if(hour>=0&&hour<10)
            rate = 0.3;
        else if(hour>=10&&hour<16)
            rate=0.4;
        else
            rate = 0.3;
        Map map = recommendUtil.recommend_score(Integer.parseInt(userId),Integer.parseInt(num));
        Map result = new HashMap();
        result.put("recipes",map.get("recipes"));
        List<SetmealInfomation> setmeals = new ArrayList<SetmealInfomation>();
        List<Integer> setmealsId = (List<Integer>) map.get("setmealsId");
        for(int i=0;i<setmealsId.size();i++)
            setmeals.add(setmealInfomationService.selectByPrimaryKey(setmealsId.get(i)));
        result.put("setmealInformation",setmeals);

        //人体需要
        Userinfo consumer = userinfoService.selectByPrimarykey(Integer.parseInt(userId));
        StandardIntake intake = standardIntakeService.getStandardIntake(consumer);
        result.put("standardIntake",intake);

        //蛋白质偏差率
        List<Double> proteinRate = new ArrayList<Double>();
        for (int i=0;i<setmeals.size();i++)
            proteinRate.add((setmeals.get(i).getSiPortein()-intake.getSiPortein()*rate)/intake.getSiPortein()*rate);
        result.put("proteinRate",proteinRate);

        //脂肪偏差率
        List<Double> fatRate = new ArrayList<Double>();
        for (int i=0;i<setmeals.size();i++)
            fatRate.add((setmeals.get(i).getSiFat()-intake.getSiEnergy()*0.25*rate)/intake.getSiEnergy()*0.25*rate);
        result.put("fatRate",fatRate);

        //热量偏差率
        List<Double> energyRate = new ArrayList<Double>();
        for (int i=0;i<setmeals.size();i++)
            energyRate.add((setmeals.get(i).getSiEnergy()-intake.getSiEnergy()*rate)/intake.getSiEnergy()*rate);
        result.put("energyRate",energyRate);

        //热量需要量
        double energyNeed = recommendUtil.energyFormula(consumer.getUserSex(),consumer.getUserWeight(),consumer.getUserHeight(),consumer.getUserAge());
        result.put("energyNeed",energyNeed);
        return result;
    }
}
