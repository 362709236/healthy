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
        List<List> setmealList = (List<List>) recommendUtil.recommend_score(Integer.parseInt(userId)).get("recipes");
        model.addAttribute("setmeals",setmealList);
        return "data_launcher";
    }
    /*
    * 测试接口
    * */
    @ResponseBody
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public Map test(@RequestParam("userId") String userId, Model model) throws Exception {
        return recommendUtil.recommend_score(Integer.parseInt(userId));
    }
}
