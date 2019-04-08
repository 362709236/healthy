package cn.cch.healthy.util;

import Jama.Matrix;
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
    public Map recommend(int userId) throws Exception {
        Userinfo consumer = recommendUtil.userinfoService.selectByPrimarykey(userId);
        Date date = new Date();
        int time=0;
        double rate=0;
        if(date.getHours()>=0&&date.getHours()<10)
        {
            time = 1;
            rate = 0.3;
        }
        else if(date.getHours()>=10&&date.getHours()<16)
        {
            time = 2;
            rate = 0.35;
        }
        else
            {
                time = 3;
                rate = 0.3;
            }

        //若用户没有填写性别和年龄   就无法推送
        if (consumer.getUserSex() == null || consumer.getUserAge() == null){
            return null;
        }

        StandardIntake intake = recommendUtil.standardIntakeService.getStandardIntake(consumer);
        List<SetmealInfomation> setMealList = recommendUtil.setmealInfomationService.SelectByTime(time);
        System.out.println(intake);
        for (int i=0;i<setMealList.size();i++)
        {
            System.out.println(setMealList.get(i).toString());
        }
        System.out.println("刚开始得到的列表长度"+setMealList.size());

        //通过能量需要筛选
        for(int i =setMealList.size()-1;i>=0;i--)
        {
            if(setMealList.get(i).getSiEnergy()>(intake.getSiEnergy()*rate+energyRange)
                    ||setMealList.get(i).getSiEnergy()<(intake.getSiEnergy()*rate-energyRange))
            {
                System.out.println("目标能量值是:"+setMealList.get(i).getSiEnergy());
                System.out.println("需要的能量值是:"+(intake.getSiEnergy()*rate-energyRange)+"到"+(intake.getSiEnergy()*rate+energyRange));
                System.out.println("第"+(i)+"个套餐不满足能量需求而被移除");
                setMealList.remove(i);
            }
        }
        System.out.println("通过能量需要筛选得到的列表长度"+setMealList.size());

        //通过蛋白质需要筛选
        for(int i =setMealList.size()-1;i>=0;i--)
        {
            if(setMealList.get(i).getSiPortein()>(intake.getSiPortein()*rate+proteinRange)
                    ||setMealList.get(i).getSiPortein()<(intake.getSiPortein()*rate-proteinRange))
            {
                setMealList.remove(i);
            }
        }
        System.out.println("通过蛋白质需要筛选得到的列表长度"+setMealList.size());
        //通过脂肪需要筛选
        double fatNeed = intake.getSiEnergy()*0.25*rate;
        for(int i =setMealList.size()-1;i>=0;i--)
        {
            if (setMealList.get(i).getSiEnergy() * 0.25 > (fatNeed + fatRange)
                    || setMealList.get(i).getSiEnergy() * 0.25 < (fatNeed - fatRange)) {
                setMealList.remove(i);
            }
        }
        System.out.println("通过脂肪需要筛选得到的列表长度"+setMealList.size());
        /*//过滤掉近3天推荐过的套餐
        List<PushInfomation> newestPush = recommendUtil.pushInfomationService.selectRecentPush(3);
        for(int i =setMealList.size()-1;i>=0;i--)
        {
            for(int j=0;j<newestPush.size();j++)
            {
                if(setMealList.get(i).getSmId()==newestPush.get(j).getSmId())
                    setMealList.remove(i);
            }
        }
        System.out.println("过滤掉近3天推荐过的套餐得到的列表长度"+setMealList.size());*/
        //过滤掉当前用户禁止食用的套餐
        List<UserIllness> UINlist = recommendUtil.userIllnessService.SelectByUserid(userId);
       /* List forbiddenList = getRemoveSetmeal.GetRemoveSetmeal(UINlist);
        for(int i =setMealList.size()-1;i>=0;i--)
        {

            for(int j=0;j<forbiddenList.size();j++)
            {
                if(setMealList.get(i).getSmId()==forbiddenList.get(i))
                    setMealList.remove(i);
            }
        }*/
        System.out.println("随机算法之前列表的长度是"+setMealList.size());
        if(setMealList.size()==0)
            return new HashMap();
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
        //平衡权重，如果有最近刚吃过的菜，权重下降
        AdwardUtil adwardUtil = new AdwardUtil();
        List<Map> records = dietRecordService.selectRecentRecord(1, 3, 2);
        adwards=adwardUtil.balanceWeight(records,adwards);
        for(int i =0;i<adwards.size();i++)
        {
            list.add(adwards.get(i).getWeight()/totalWeight);
        }
        AliasUtil method = new AliasUtil(list);
        int index = method.next();
        System.out.println("推荐的套餐id是"+adwards.get(index).getSmId());
        List<SetMeal> setMealContent = recommendUtil.setMealService.SelectBySMid(adwards.get(index).getSmId());
        Map map = new HashMap();
        List name = new ArrayList();    
        String SM_name = setmealInfomationService.FindSMnameByPrimaryKey(setMealContent.get(0).getSmId());

        for(int i=0;i<setMealContent.size();i++)
        {

            name.add(recommendUtil.recipesService.getName(setMealContent.get(i).getRecipesId()));
        }
        map.put("smId",setMealContent.get(0).getSmId());
        map.put("smname",SM_name);
        map.put("nameList",name);
        //添加推送记录
        PushInfomation pushInfomation = new PushInfomation();
        pushInfomation.setUserId(consumer.getUserId());
        pushInfomation.setSmId(adwards.get(index).getSmId());
        pushInfomation.setPiDate(new Date());
        pushInfomation.setPiTime(time);
        recommendUtil.pushInfomationService.addNewPush(pushInfomation);
        return map;
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
    /*
    * 新的推荐算法
    * */
    public void recommend_score(int userId)
    {
        Userinfo consumer = recommendUtil.userinfoService.selectByPrimarykey(userId);
        //若用户没有填写性别和年龄   就无法推送
        if (consumer.getUserSex() == null || consumer.getUserAge() == null){
            return;
        }
        //识别当前是早餐、午餐还是晚餐
        int hour = Calendar.HOUR;
        int time=1;
        double rate=0;
        if(hour>=0&&hour<10)
        {
            time = 1;
            rate = 0.3;
        }
        else if(hour>=10&&hour<16)
        {
            time = 2;
            rate=0.4;
        }
        else
            {
                time = 3;
                rate = 0.3;
            }
        //根据中国膳食推荐摄入表获得当前用户推荐摄入量
        StandardIntake intake = recommendUtil.standardIntakeService.getStandardIntake(consumer);
        //获得数据库中当前时间段所有套餐
        List<SetmealInfomation> setMealList = recommendUtil.setmealInfomationService.SelectByTime(time);
        //创建套餐分数二维数组
        double[][] score = new double[6][setMealList.size()];

        //根据蛋白质打分
        for (int i=0;i<score[0].length;i++)
        {
            double distance = Math.abs(setMealList.get(i).getSiPortein()-intake.getSiPortein()*rate);
            if(distance>30)
            {
                score[0][i]=1;
                continue;
            }
            score[0][i]=10-distance/3;
        }
        //根据能量打分
        for (int i=0;i<score[1].length;i++)
        {
        //    System.out.println("套餐含有的能量="+setMealList.get(i).getSiEnergy());
         //   System.out.println("推荐的能量="+intake.getSiEnergy());
            double distance = Math.abs(setMealList.get(i).getSiEnergy()-intake.getSiEnergy()*rate);
            if(distance>30)
            {
                score[0][i]=1;
                continue;
            }
            score[1][i]=10-distance;
        }
        double [][]weight = {{2},{3},{4},{5},{6},{7}};
        Matrix weightMatrix = new Matrix(weight);
        Matrix matrix = new Matrix(score);
        matrix.times(weightMatrix);
        matrix.print(4,2);
    }
}
