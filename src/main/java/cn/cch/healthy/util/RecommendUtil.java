package cn.cch.healthy.util;

import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@Component
public class RecommendUtil {
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
    AdwardUtil adwardUtil = new AdwardUtil();

    GetRemoveSetmeal getRemoveSetmeal = new GetRemoveSetmeal();
    //能量需要的上下波动范围
    @Value("${range.energy}")
    private double energyRange;

    //蛋白质需要的上下波动范围
    @Value("${range.protein}")
    private double proteinRange;

    //脂肪需要的上下波动范围
    @Value("${range.fat}")
    private double fatRange;

    public static RecommendUtil recommendUtil;

    /*
     * 描述：推荐给用户一个菜谱
     * */
    public List<Map> recommend(int userId)
    {
        Userinfo consumer = recommendUtil.userinfoService.selectByPrimarykey(userId);
        Date date = new Date();
        int time=0;
        if(date.getHours()>=0&&date.getHours()<10)
            time = 1;
        else if(date.getHours()>=10&&date.getHours()<16)
            time = 2;
        else
            time = 3;

        //若用户没有填写性别和年龄   就无法推送
        if (consumer.getUserSex() == null || consumer.getUserAge() == null){
            return null;
        }

        StandardIntake intake = recommendUtil.standardIntakeService.getStandardIntake(consumer);
        List<SetmealInfomation> setMealList = recommendUtil.setmealInfomationService.SelectByTime(2);
        System.out.println("刚开始得到的列表长度"+setMealList.size());
        //通过能量需要筛选
        for(int i =0;i< setMealList.size();i++)
        {
            if(setMealList.get(i).getSiEnergy()>(intake.getSiEnergy()+energyRange)
                    ||setMealList.get(i).getSiEnergy()<(intake.getSiEnergy()-energyRange))
            {
                setMealList.remove(i);
            }
        }
        System.out.println("通过能量需要筛选得到的列表长度"+setMealList.size());
        //通过蛋白质需要筛选
        for(int i =0;i< setMealList.size();i++)
        {
            if(setMealList.get(i).getSiPortein()>(intake.getSiPortein()+proteinRange)
                    ||setMealList.get(i).getSiPortein()<(intake.getSiPortein()-proteinRange))
            {
                setMealList.remove(i);
            }
        }
        System.out.println("通过蛋白质需要筛选得到的列表长度"+setMealList.size());
        //通过脂肪需要筛选
        double fatNeed = intake.getSiEnergy()*0.25;
        for(int i =0;i< setMealList.size();i++) {
            if (setMealList.get(i).getSiEnergy() * 0.25 > (fatNeed + fatRange)
                    || setMealList.get(i).getSiEnergy() * 0.25 < (fatNeed - fatRange)) {
                setMealList.remove(i);
            }
        }
        System.out.println("通过脂肪需要筛选得到的列表长度"+setMealList.size());
        //过滤掉近3天推荐过的套餐
        List<PushInfomation> newestPush = recommendUtil.pushInfomationService.selectRecentPush(0);
        for(int i=0;i<setMealList.size();i++)
        {
            for(int j=0;j<newestPush.size();j++)
            {
                if(setMealList.get(i).getSmId()==newestPush.get(j).getSmId())
                    setMealList.remove(i);
            }
        }
        System.out.println("过滤掉近3天推荐过的套餐得到的列表长度"+setMealList.size());
        //过滤掉当前用户禁止食用的套餐
        List<UserIllness> UINlist = recommendUtil.userIllnessService.SelectByUserid(userId);
        List forbiddenList = getRemoveSetmeal.GetRemoveSetmeal(UINlist);
        for(int i=0;i<setMealList.size();i++)
        {

            for(int j=0;j<newestPush.size();j++)
            {
                if(setMealList.get(i).getSmId()==forbiddenList.get(i))
                    setMealList.remove(i);
            }
        }
        System.out.println("随机算法之前列表的长度是"+setMealList.size());
        /*
         * 使用alias算法开始抽奖
         * */
        List<Adward> adwards = new ArrayList<Adward>();
        int totalWeight=0;
        List list=new ArrayList();
        //设置奖品的名称(套餐id)和权重
        for(int i=0;i<setMealList.size();i++)
        {
            Adward adward = new Adward(setMealList.get(i).getSmId());
            totalWeight+=adward.getWeight();
            adwards.add(adward);
        }
        for(int i =0;i<adwards.size();i++)
        {
            list.add(adwards.get(i).getWeight()/totalWeight);
        }
        AliasUtil method = new AliasUtil(list);
        int index = method.next();
        System.out.println("推荐的套餐id是"+adwards.get(index).getSmId());
        List<SetMeal> setMealContent = recommendUtil.setMealService.SelectBySMid(adwards.get(index).getSmId());
        List<Map> mapList = new ArrayList<>();
        for(int i=0;i<setMealContent.size();i++)
        {
            Map map = new HashMap();
            map.put("recipes",setMealContent.get(i));
            map.put("name",recommendUtil.recipesService.getName(setMealContent.get(i).getRecipesId()));
            mapList.add(map);
        }

        //添加推送记录
        PushInfomation pushInfomation = new PushInfomation();
        pushInfomation.setUserId(consumer.getUserId());
        pushInfomation.setSmId(adwards.get(index).getSmId());
        pushInfomation.setPiDate(new Date());
        pushInfomation.setPiTime(time);
        recommendUtil.pushInfomationService.addNewPush(pushInfomation);
        return mapList;
    }
    @PostConstruct
    public void init() {
        recommendUtil = this;
        recommendUtil.userIllnessService = this.userIllnessService;
        recommendUtil.standardIntakeService = this.standardIntakeService;
        recommendUtil.setmealInfomationService = this.setmealInfomationService;
        recommendUtil.pushInfomationService = this.pushInfomationService;
        recommendUtil.setMealService = this.setMealService;
        recommendUtil.recipesService = this.recipesService;
        recommendUtil.dietRecordService = this.dietRecordService;
        recommendUtil.userIllnessService = this.userIllnessService;
    }
}
