package cn.cch.healthy.util;

import Jama.Matrix;
import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;


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
    @Autowired
    FoodillnessService foodillnessService;
    @Autowired
    InterestService interestService;
    @Autowired
    FoodFormulaService foodFormulaService;
    @Autowired
    RecipesIllnessService recipesIllnessService;
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
        recommendUtil.interestService = this.interestService;
        recommendUtil.foodillnessService = this.foodillnessService;
        recommendUtil.foodFormulaService = this.foodFormulaService;
        recommendUtil.recipesIllnessService = recipesIllnessService;
    }
    /*
    * 推荐算法
    * */
    public Map recommend_score(int userId) throws Exception {
        Userinfo consumer = recommendUtil.userinfoService.selectByPrimarykey(userId);
        System.out.println(consumer.toString());
        //若用户没有填写性别和年龄   就无法推送
        if (consumer.getUserSex() == null || consumer.getUserAge() == null){
            return null;
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
        List<Map> eatingRecord = dietRecordService.selectRecentRecord(userId,3,time);
        System.out.println("食用记录"+eatingRecord.toString());
        double [] score = new double[setMealList.size()];
        for(int i=0;i<setMealList.size();i++)
        {
            score[i]=mark(consumer,setMealList.get(i),intake,rate,time,eatingRecord);
        }
        //从大到小排序
        double temp;
        SetmealInfomation tempSM;
        for (int i=0;i<3;i++)
        {
            for(int j=i+1;j<score.length;j++)
            {
                if(score[i]<score[j])
                {
                    temp = score[i];
                    score[i] = score[j];
                    score[j]=temp;
                    tempSM = setMealList.get(i);
                    setMealList.set(i,setMealList.get(j));
                    setMealList.set(j,tempSM);
                }
            }
        }
        for(int i=0;i<setMealList.size();i++)
        {
            System.out.println("套餐"+i+"的最终得分为"+score[i]);
        }
        //添加推送记录
        PushInfomation pushInfomation = new PushInfomation();
        pushInfomation.setUserId(consumer.getUserId());
        pushInfomation.setSmId(setMealList.get(0).getSmId());
        pushInfomation.setPiDate(new Date());
        pushInfomation.setPiTime(time);
        recommendUtil.pushInfomationService.addNewPush(pushInfomation);

        Map map = new HashMap();
        List<List> setmeals = new ArrayList();
        for(int i=0;i<3;i++)
        {
            List<Integer> recipeList = setMealService.selectRecipeId(setMealList.get(i).getSmId());
            List recipeName = new ArrayList();
            for(int j=0;j<recipeList.size();j++)
            {
                recipeName.add(recipesService.getName(recipeList.get(j)));
            }
            setmeals.add(recipeName);
        }
        map.put("recipes",setmeals);
        return map;
    }



    @Value("${PROTEINWEIGHT}")
    private double PROTEINWEIGHT;
    @Value("${ENERGYWEIGHT}")
    private double ENERGYWEIGHT;
    @Value("${RECENTWEIGHT}")
    private double RECENTWEIGHT;
    @Value("${INTERESTWEIGHT}")
    private double INTERESTWEIGHT;
    @Value("${ILLNESSWEIGHT}")
    private double ILLNESSWEIGHT;
    /*
    * 描述：为一个套餐打分
    * */
    public double mark(Userinfo consumer,SetmealInfomation setmeal,StandardIntake intake,double rate,int time,
                       List<Map> eatingRecord) throws Exception {
        double distance;
        //套餐中的菜品列表
        List recipeList = setMealService.selectRecipeId(setmeal.getSmId());


        //蛋白质打分
        distance = Math.abs(setmeal.getSiPortein()-intake.getSiPortein()*rate);
        double score_protein = 10-distance/3;
        if (score_protein<0)
            score_protein=0;
        System.out.println("蛋白质打分完成！分数为"+score_protein);
        //能量打分
        distance = Math.abs(setmeal.getSiEnergy()*0.8-energyFormula(consumer.getUserSex()
                ,consumer.getUserWeight(),consumer.getUserHeight(),consumer.getUserAge())*rate);
        double score_energy = 10-distance/30;
        if(score_energy<0)
            score_energy=0;
        System.out.println("能量打分完成！分数为"+score_energy);
        //近期是否吃过相同菜品打分
        double deScore=0;
        for(int i=0;i<recipeList.size();i++)
        {
            for (int j=0;j<eatingRecord.size();j++)
            {
                if (Integer.parseInt(recipeList.get(i).toString())
                        ==Integer.parseInt(eatingRecord.get(j).get("recipeId").toString()))
                    deScore += Double.parseDouble(eatingRecord.get(j).get("deWeight").toString());
            }
        }
        double score_recent = deScore;
        if(score_recent>10)
            score_recent=10;
        System.out.println("近期吃过的菜品打分完成！分数为"+score_recent);
        //食堂剩余量打分
        //是否符合用户爱好打分
        double matchDegree = interestService.match(setmeal.getSmId(),consumer.getUserId())*2;
        double score_match = matchDegree;
        if (score_match>10)
            score_match=10;
        System.out.println("用户爱好打分完成！分数为"+score_match);

        //根据用户当前疾病打分
        List<Integer> illness = userIllnessService.SelectByUserid(consumer.getUserId());
            //用户当前禁止吃的食材列表
        List<Integer> forbbiden_food = new ArrayList<Integer>();
        for (int i=0;i<illness.size();i++)
        {
            List list = foodillnessService.SelectByillId(illness.get(i));
            forbbiden_food.addAll(list);
        }
            //禁止吃的食材做成的食物列表
        List<Integer> forbbiden_recipe = new ArrayList<Integer>();
        for (int i=0;i<forbbiden_food.size();i++)
        {
            List list = foodFormulaService.selectRecipeIdByFoodid(forbbiden_food.get(i));
            forbbiden_recipe.addAll(list);
        }
        for (int i=0;i<illness.size();i++)
            forbbiden_recipe.addAll(recipesIllnessService.selectRecipeIdByillId(illness.get(i)));
            //删除重复元素
        for ( int  i  =   0 ; i  <  forbbiden_recipe.size()  -   1 ; i ++ )  {
            for  ( int  j  =  forbbiden_recipe.size()  -   1 ; j  >  i; j -- )  {
                if  (forbbiden_recipe.get(j)==forbbiden_recipe.get(i))  {
                    forbbiden_recipe.remove(j);
                }
            }
        }
     //   for(int i=0;i<forbbiden_recipe.size();i++)
     //       System.out.println("禁止吃菜品"+forbbiden_recipe.get(i));
        double score_illness =0;
        for(int i=0;i<recipeList.size();i++)
        {
            for(int j=0;j<forbbiden_recipe.size();j++)
            {
                if(recipeList.get(i)==forbbiden_recipe.get(j))
                    score_illness+=5;
            }
        }
        if (score_illness>10)
            score_illness=10;
        System.out.println("用户疾病打分完成！分数为"+score_illness);

        //计算最终得分
        double finalScore=score_protein*PROTEINWEIGHT+score_energy*ENERGYWEIGHT
                +score_recent*RECENTWEIGHT+score_match*INTERESTWEIGHT+
                0*ILLNESSWEIGHT;
        System.out.println("---------------打分成功---------"+finalScore+"--------");
        return finalScore;
    }

    private double energyFormula(String sex,double weight,double height,int age)
    {
        if (sex.equals("男"))
            return 66+13.7*weight+5*height+6.8*age;
        else if(sex.equals("女"))
            return 655+9.6*weight+1.8*height+4.7*age;
        else
            return -1;
    }
}
