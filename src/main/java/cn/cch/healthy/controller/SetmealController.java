package cn.cch.healthy.controller;

import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealInfomationService setmealInfomationService;

    @Autowired
    RecipesService recipesService;

    @Autowired
    FoodFormulaService foodFormulaService;

    @Autowired
    FoodService foodService;

    @Autowired
    SetMealService setMealService;

    @Autowired
    InterestService interestService;

    private int start=30;


    //套餐自动生成（一荤一素）-------正餐
   // @RequestMapping("autoForm_1_1")
    public String autoForm_1_1()
    {
        List<Recipes> meatDishList =recipesService.getRecipesByType("荤菜");
        List<Recipes> vegeDishList = recipesService.getRecipesByType("素菜");
        for (int i=0;i<meatDishList.size();i++)
        {
            for (int j=0;j<vegeDishList.size();j++)
            {
                int sm_id = insertSM("中餐"+start);
                int recipesArray[] = {meatDishList.get(i).getRecipesId(),vegeDishList.get(j).getRecipesId()};
                updateSM(recipesArray,sm_id,2);
                System.out.println("成功添加中餐"+(start++));
            }
        }
        return "成功！";
    }
    //套餐自动生成（一荤一素一汤）--------正餐
  //  @RequestMapping("autoForm_1_1_1")
    public String autoForm_1_1_1()
    {
        List<Recipes> meatDishList =recipesService.getRecipesByType("荤菜");
        List<Recipes> vegeDishList = recipesService.getRecipesByType("素菜");
        List<Recipes> soupDishList = recipesService.getRecipesByType("汤");
        for (int i=0;i<meatDishList.size();i++)
        {
            for (int j=0;j<vegeDishList.size();j++)
            {
                for(int z=0;z<soupDishList.size();z++)
                {
                    int soupId = soupDishList.get(z).getRecipesId();
                    int vegeId = vegeDishList.get(j).getRecipesId();
                    int meatId = meatDishList.get(i).getRecipesId();
                    if (soupId!=vegeId&& soupId!=meatId)
                    {
                        int sm_id = insertSM("中餐"+start);
                        int recipesArray[] = {meatId,vegeId, soupId};
                        updateSM(recipesArray,sm_id,2);
                        System.out.println("成功添加中餐"+(start++));
                    }
                }
            }
        }
        return "成功！";
    }

    //套餐自动生成（一面条一配菜）-------早餐
  //  @RequestMapping("autoForm_break_1_1")
    public String autoForm_break_1_1()
    {
        List<Recipes> noodleList =recipesService.getRecipesByType("面条");
        for (int i=0;i<noodleList.size();i++)
        {
                int sm_id = insertSM("早餐"+start);
                int recipesArray[] = {noodleList.get(i).getRecipesId()};
                updateSM(recipesArray,sm_id,1);
                System.out.println("成功添加早餐"+(start++));
        }
        return "成功！";
    }

    //套餐自动生成（一馒头一配菜一饮料）-------早餐
   // @RequestMapping("autoForm_break_1_1_1")
    public String autoForm_break_1_1_1()
    {
        List<Recipes> baoList =recipesService.getRecipesByType("馒头");
        List<Recipes> subList = recipesService.getRecipesByType("配菜");
        List<Recipes> drinkList = recipesService.getRecipesByType("饮料");
        for (int i=0;i<baoList.size();i++)
        {
            for (int j=0;j<subList.size();j++)
            {
                for(int z=0;z<drinkList.size();z++)
                {
                    int drinkId = drinkList.get(z).getRecipesId();
                    int subId = subList.get(j).getRecipesId();
                    int baoId = baoList.get(i).getRecipesId();
                    int sm_id = insertSM("早餐"+start);
                    int recipesArray[] = {baoId,subId, drinkId};
                    updateSM(recipesArray,sm_id,1);
                    System.out.println("成功添加早餐"+(start++));
                }
            }
        }
        return "成功！";
    }

    //套餐自动生成（一馒头一饮料）-------早餐
   // @RequestMapping("autoForm_break2_1_1_1")
    public String autoForm_break2_1_1()
    {
        List<Recipes> baoList =recipesService.getRecipesByType("馒头");
        List<Recipes> drinkList = recipesService.getRecipesByType("饮料");
        for (int i=0;i<baoList.size();i++)
        {
                for(int z=0;z<drinkList.size();z++)
                {
                    int drinkId = drinkList.get(z).getRecipesId();
                    int baoId = baoList.get(i).getRecipesId();
                    int sm_id = insertSM("早餐"+start);
                    int recipesArray[] = {baoId, drinkId};
                    updateSM(recipesArray,sm_id,1);
                    System.out.println("成功添加早餐"+(start++));
                }
        }
        return "成功！";
    }
    //新增套餐
    //@RequestMapping("insert")
    public int insertSM(String name){
        SetmealInfomation SmI = new SetmealInfomation();
        SmI.setSmName(name);
        SmI.setRightTime(0);
        SmI.setSiEnergy(0.0);
        SmI.setSiPortein(0.0);
        SmI.setSiFat(0.0);
        SmI.setSiVitaminA(0);
        SmI.setSiVitaminB1(0);
        SmI.setSiVitaminB2(0);
        SmI.setSiVitaminC(0);
        SmI.setSiCa(0);
        SmI.setSiCholesterol(0);
        SmI.setSiVitaminE(0);
        SmI.setSiFe(0);
        SmI.setSiMg(0);
        SmI.setSiZn(0);
        setmealInfomationService.insert(SmI);
        int id = SmI.getSmId();
        return id;
    }

    //搭配套餐
   // @RequestMapping("update")
    public String updateSM(int[] RecipesArray,int SM_id,
                           int Righttime){
        List<SetMeal> old_SmIlist = setMealService.SelectBySMid(SM_id);
        ArrayList<Integer> insertList = new ArrayList();
        ArrayList<Integer> deleteList = new ArrayList();
        ArrayList<Integer> restList = new ArrayList();

        if (RecipesArray.length != 0){
            for (int i = 0;i<RecipesArray.length;i++){
                insertList.add(RecipesArray[i]);
            }
        }else{
            if (old_SmIlist.size() != 0){
                for (int i = 0;i<old_SmIlist.size();i++){
                    int old_SmIid = old_SmIlist.get(i).getRecipesId();
                    SetMeal SM = new SetMeal();
                    SM.setRecipesId(old_SmIid);
                    SM.setSmId(SM_id);
                    setMealService.DeleteByRecipesId(SM);
                }
                return "套餐清空！";
            }else{
                return "套餐是空";
            }
        }

        if (old_SmIlist.size() != 0 && RecipesArray.length != 0){
            for (int i = 0;i<old_SmIlist.size();i++){
                int old_SmIid = old_SmIlist.get(i).getRecipesId();
                deleteList.add(old_SmIid);
                for (int m = 0;m<RecipesArray.length;m++){
                    if (RecipesArray[m]==old_SmIid){
                        restList.add(old_SmIid);
                        deleteList.remove(Integer.valueOf(old_SmIid));
                    }
                }
            }

            if (restList.size() != 0){
                for (int m = 0;m<insertList.size();m++){
                    int in = insertList.get(m);
                    for (int n = 0;n<restList.size();n++){
                        int out = restList.get(n);
                        if (out==in){
                            insertList.remove(Integer.valueOf(in));
                            m--;
                            break;
                        }
                    }
                }
            }
        }
        if (insertList.size() != 0){
            for (int i = 0;i<insertList.size();i++){
                int insertRecipesId = insertList.get(i);
                SetMeal SM = new SetMeal();
                SM.setRecipesId(insertRecipesId);
                SM.setSmId(SM_id);
                setMealService.Insert(SM);

                //添加标签
                String tags[]=recipesService.getTypeByid(insertRecipesId).split(",");
               // System.out.println("所有标签"+recipesService.getTypeByid(insertRecipesId));
                for(int j=0;j<tags.length;j++)
                {
                    int id = interestService.GetID(tags[j]);
                    Setmeal_Interest setmeal_interest = new Setmeal_Interest(SM_id,id);
                    interestService.insertSetmealIntest(setmeal_interest);
                }
            }
        }
        if (deleteList.size() != 0){
            for (int i = 0;i<deleteList.size();i++){
                int deleteRecipesId = deleteList.get(i);
                SetMeal SM = new SetMeal();
                SM.setRecipesId(deleteRecipesId);
                SM.setSmId(SM_id);
                setMealService.DeleteByRecipesId(SM);

                //删除标签
            }
        }

        //营养含量值
        double Food_fat = 0;
        double Food_protein = 0;
        double Food_energy = 0;
        double Food_vitamin_A = 0;
        double Food_vitamin_B_1 = 0;
        double Food_vitamin_B_2 = 0;
        double Food_vitamin_C = 0;
        double Food_vitamin_E = 0;
        double Food_Ca = 0;
        double Food_Mg = 0;
        double Food_Fe = 0;
        double Food_Zn = 0;
        double Food_cholesterol = 0;

        List<SetMeal> new_SmIlist = setMealService.SelectBySMid(SM_id);
        for (int m = 0;m<new_SmIlist.size();m++){
            int new_SmIid = new_SmIlist.get(m).getRecipesId();
            Recipes recipes = recipesService.SelectByPrimaryKey(new_SmIid);

            //计算套餐营养含量
            Food_fat += recipes.getRecipesFat();
            Food_protein += recipes.getRecipesProtein();
            Food_energy += recipes.getRecipesEnergy();
            Food_vitamin_A += recipes.getRecipesVitaminA();
            Food_vitamin_B_1 += recipes.getRecipesVitaminB1();
            Food_vitamin_B_2 += recipes.getRecipesVitaminB2();
            Food_vitamin_C += recipes.getRecipesVitaminC();
            Food_vitamin_E += recipes.getRecipesVitaminE();
            Food_Ca += recipes.getRecipesCa();
            Food_Mg += recipes.getRecipesMg();
            Food_Fe += recipes.getRecipesFe();
            Food_Zn += recipes.getRecipesZn();
            Food_cholesterol += recipes.getRecipesCholesterol();

        }

        System.out.println("Food_energy："+Food_energy);
        System.out.println("Food_protein："+Food_protein);
        System.out.println("Food_fat："+Food_fat);
        System.out.println("Food_vitamin_A："+Food_vitamin_A);
        System.out.println("Food_vitamin_B_1："+Food_vitamin_B_1);
        System.out.println("Food_vitamin_B_2："+Food_vitamin_B_2);
        System.out.println("Food_vitamin_C："+Food_vitamin_C);
        System.out.println("Food_vitamin_E："+Food_vitamin_E);
        System.out.println("Food_Ca："+Food_Ca);
        System.out.println("Food_Mg："+Food_Mg);
        System.out.println("Food_Fe："+Food_Fe);
        System.out.println("Food_Zn："+Food_Zn);
        System.out.println("Food_cholesterol："+Food_cholesterol);

        System.out.println("-----------------------------------");

        SetmealInfomation SmI = new SetmealInfomation();
        SmI.setSmId(SM_id);
        SmI.setSiFat(Food_fat);
        SmI.setSiPortein(Food_protein);
        SmI.setSiEnergy(Food_energy);

        SetmealController SmC = new SetmealController();

        int vitamin_A = SmC.JudgeHML(1,Food_vitamin_A,Righttime);
        SmI.setSiVitaminA(vitamin_A);
        int vitamin_B_1 = SmC.JudgeHML(2,Food_vitamin_B_1,Righttime);
        SmI.setSiVitaminB1(vitamin_B_1);
        int vitamin_B_2 = SmC.JudgeHML(3,Food_vitamin_B_2,Righttime);
        SmI.setSiVitaminB2(vitamin_B_2);
        int vitamin_C = SmC.JudgeHML(4,Food_vitamin_C,Righttime);
        SmI.setSiVitaminC(vitamin_C);
        int vitamin_E = SmC.JudgeHML(5,Food_vitamin_E,Righttime);
        SmI.setSiVitaminE(vitamin_E);
        int Ca = SmC.JudgeHML(6,Food_Ca,Righttime);
        SmI.setSiCa(Ca);
        int Mg = SmC.JudgeHML(7,Food_Mg,Righttime);
        SmI.setSiMg(Mg);
        int Fe = SmC.JudgeHML(8,Food_Fe,Righttime);
        SmI.setSiFe(Fe);
        int Zn = SmC.JudgeHML(9,Food_Zn,Righttime);
        SmI.setSiZn(Zn);
        int Cholesterol = SmC.JudgeHML(10,Food_cholesterol,Righttime);
        SmI.setSiCholesterol(Cholesterol);

        SmI.setRightTime(Righttime);
        int end = setmealInfomationService.UpdateByPrimaryKeySelective(SmI);
        if (end == 1){
            //json转化
            JSONObject jsonObject = JSONObject.fromObject(SmI);
            System.out.println("输出的结果是：" + jsonObject);
            //3、将json对象转化为json字符串
            String result = jsonObject.toString();
            return result;
        }else{
            return "失败";
        }
    }

    /*判断营养素含量 高 中 低
    * type：
    * 1:A  2:B1 3:B2 4:C 5:E
    * 6:Ca 7:Mg 8:Fe 9:Zn 10:cholesterol
    * */
    public int JudgeHML(int type,double content,int time){
        int Grade = 0;
        double ratio = 0;

        switch (time){
            case 1:
                ratio = 0.3;
                break;
            case 2:
                ratio = 0.4;
                break;
            case 3:
                ratio = 0.3;
                break;
        }

        switch (type){
            case 1:
                if (content < 750*ratio){
                    Grade = 1;
                }else if (content >= 750*ratio && content < 900*ratio){
                    Grade = 2;
                }else if (content >= 900*ratio){
                    Grade = 3;
                }
                break;
            case 2:
                if (content < 1.2*ratio){
                    Grade = 1;
                }else if (content >= 1.2*ratio && content < 1.5*ratio){
                    Grade = 2;
                }else if (content >= 1.5*ratio){
                    Grade = 3;
                }
                break;
            case 3:
                if (content < 1.2*ratio){
                    Grade = 1;
                }else if (content >= 1.2*ratio && content < 1.5*ratio){
                    Grade = 2;
                }else if (content >= 1.5*ratio){
                    Grade = 3;
                }
                break;
            case 4:
                if (content < 75*ratio){
                    Grade = 1;
                }else if (content >= 75*ratio && content < 100*ratio){
                    Grade = 2;
                }else if (content >= 100*ratio){
                    Grade = 3;
                }
                break;
            case 5:
                if (content < 9*ratio){
                    Grade = 1;
                }else if (content >= 9*ratio && content < 12*ratio){
                    Grade = 2;
                }else if (content >= 12*ratio){
                    Grade = 3;
                }
                break;
            case 6:
                if (content < 900*ratio){
                    Grade = 1;
                }else if (content >= 900*ratio && content < 1200*ratio){
                    Grade = 2;
                }else if (content >= 1200*ratio){
                    Grade = 3;
                }
                break;
            case 7:
                if (content < 300*ratio){
                    Grade = 1;
                }else if (content >= 300*ratio && content < 350*ratio){
                    Grade = 2;
                }else if (content >= 350*ratio){
                    Grade = 3;
                }
                break;
            case 8:
                if (content < 12*ratio){
                    Grade = 1;
                }else if (content >= 12*ratio && content < 20*ratio){
                    Grade = 2;
                }else if (content >= 20*ratio){
                    Grade = 3;
                }
                break;
            case 9:
                if (content < 9*ratio){
                    Grade = 1;
                }else if (content >= 9*ratio && content < 15*ratio){
                    Grade = 2;
                }else if (content >= 15*ratio){
                    Grade = 3;
                }
                break;
            case 10:
                if (content < 60*ratio){
                    Grade = 1;
                }else if (content >= 60*ratio && content < 300*ratio){
                    Grade = 2;
                }else if (content >= 300*ratio){
                    Grade = 3;
                }
                break;
        }
        return Grade;
    }

}
