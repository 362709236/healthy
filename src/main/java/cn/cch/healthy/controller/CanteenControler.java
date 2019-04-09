package cn.cch.healthy.controller;

import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/*
 * 食堂接口
 * */

@RestController
@RequestMapping("canteen")
public class CanteenControler {

    @Autowired
    FoodFormulaService foodFormulaService;

    @Autowired
    RecipesService recipesService;

    @Autowired
    SetmealInfomationService setmealInfomationService;

    @Autowired
    SetMealService setMealService;

    @Autowired
    DietRecordService dietRecordService;

    /*
    * 菜谱配方接口
    * */
    @RequestMapping("insertFormula")
    public String insertFormula(@RequestParam("FoodId") int FoodId,@RequestParam("RecipesId") int RecipesId,
                                @RequestParam("FoodNumber") String FoodNumber){
        Double foodnumber = Double.valueOf(FoodNumber);
        FoodFormula FF = new FoodFormula();
        FF.setRecipesId(RecipesId);
        FF.setFoodId(FoodId);
        FF.setFoodNumber(foodnumber);
        int end = foodFormulaService.insert(FF);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("deleteFormula")
    public String deleteFormula(@RequestParam("FormulaId") int FormulaId){
        int end = foodFormulaService.DeleteByPrimaryKey(FormulaId);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("updateFormula")
    public String updateFormula(@RequestParam("FormulaId") int FormulaId,@RequestParam("FoodId") int FoodId,
    @RequestParam("FoodNumber") String FoodNumber){
        double foodnumber = Double.valueOf(FoodNumber);
        FoodFormula FF = new FoodFormula();
        FF.setFfId(FormulaId);
        FF.setFoodId(FoodId);
        FF.setFoodNumber(foodnumber);
        int end = foodFormulaService.UpdateByPrimaryKeySelective(FF);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("selectRecipesFormula")
    public List selectRecipesFormula(@RequestParam("RecipesId") int RecipesId){
        List<FoodFormula> FFlist = foodFormulaService.SelectByRecipesId(RecipesId);
        if (FFlist.size() != 0){
            List resultlist = new ArrayList();
            for (int i = 0;i<FFlist.size();i++){
                Map map = new HashMap();
                int recipes_id = FFlist.get(i).getRecipesId();
                String recipes_name = recipesService.getName(recipes_id);

                map.put("formulaId",FFlist.get(i).getFfId());
                map.put("recipes_name",recipes_name);
                map.put("recipes_number",FFlist.get(i).getFoodNumber());
                resultlist.add(map);
            }
            return resultlist;
        }
        return null;
    }


    public String selectAllFormula(){

        return "";
    }

    /*
    * 设置套餐接口
    * SetmealController  updateSM()
    * */
    @RequestMapping("deleteSM")
    public String deleteSM(@RequestParam("SMId") int SMId){
        int end1 = setMealService.DeleteBySMid(SMId);
        int end2 =setmealInfomationService.DeleteByPrimaryKey(SMId);
        if (end1 == 1 && end2 == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("selectSM")
    public List selectSM(@RequestParam("SMId") int SMId){
        List<SetMeal> setmealList = setMealService.SelectBySMid(SMId);
        String SI_name = setmealInfomationService.FindSMnameByPrimaryKey(SMId);
        List resultlist = new ArrayList();
        Map map1 = new HashMap();
        map1.put("SI_name",SI_name);
        resultlist.add(map1);
        if (setmealList.size() != 0){
            for (int i = 0;i<setmealList.size();i++){
                Map map = new HashMap();
                int recipes_id = setmealList.get(i).getRecipesId();
                String recipes_name = recipesService.getName(recipes_id);
                map.put("recipes_name",recipes_name);
                resultlist.add(map);
            }
            return setmealList;
        }

        return null;
    }

    @RequestMapping("selectAllSMname")
    public List<String> selectAllSMname(){
        List<String> nameList = setmealInfomationService.SelectAllForName();
        return nameList;
    }

    /*
    * 菜品接口
    * */
    @RequestMapping("insertRecipes")
    public String insertRecipes(@RequestParam("Recipes_nmae") String Recipes_nmae){
        Recipes recipes = new Recipes();
        recipes.setRecipesName(Recipes_nmae);
        int end = recipesService.insert(recipes);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("deleteRecipes")
    public String deleteRecipes(@RequestParam("Recipes_id") int Recipes_id){
        int end1 = recipesService.DeleteByPrimaryKey(Recipes_id);
        int end2 = foodFormulaService.DeleteByRecipesId(Recipes_id);
        if (end1 == 1 && end2 == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("updateRecipesName")
    public String updateRecipesName(@RequestParam("Recipes_id") int Recipes_id,
                                    @RequestParam("Recipes_name") String Recipes_name){
        Recipes recipes = recipesService.SelectByPrimaryKey(Recipes_id);
        recipes.setRecipesName(Recipes_name);
        int end = recipesService.UpdateByPrimaryKeySelective(recipes);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    public String selectRecipes(){

        return "";
    }

    @RequestMapping("selectAllRecipes")
    public List<Recipes> selectAllRecipes(){
        List<Recipes> recipes_list = recipesService.Findall();
        return recipes_list;
    }

    /*
    * 食堂饮食记录接口
    * */
    @RequestMapping("insertDietRecord")
    public String insertDietRecord(@RequestParam("RecipesArray") int[] RecipesArray,
                                   @RequestParam("User_id") int User_id){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datestr = sdf.format(date);
        int time = 0;
        if(date.getHours()>=0&&date.getHours()<10)
        {
            time = 1;
        }
        else if(date.getHours()>=10&&date.getHours()<16)
        {
            time = 2;
        }
        else
        {
            time = 3;
        }

        DietRecord DR = new DietRecord();
        DR.setDrDate(date);
        DR.setDrTime(time);
        DR.setUserId(User_id);
        int end1 = dietRecordService.insert(DR);

        if (end1 == 1){
            List<DietRecord> DRlist = dietRecordService.SelectByDate(datestr,User_id);
            int DR_id = DRlist.get(0).getDrId();
            int i = 0;
            while (i<RecipesArray.length){
                DietRecord_Sub DRS = new DietRecord_Sub();
                DRS.setDrId(DR_id);
                DRS.setRecipesId(RecipesArray[i]);
                int end2 = dietRecordService.insertsub(DRS);
                if (end2 != 0){
                    return "失败";
                }
                i++;
            }
            return "成功";
        }
        return "失败";
    }

    public String selectDietRecord(){

        return "";
    }

    @RequestMapping("selectAllDietRecordSub")
    public List selectAllDietRecordSub(@RequestParam("DR_id") int DR_id){
        List<DietRecord_Sub> DRSlist = dietRecordService.SelectByDRid(DR_id);
        List<String> resultlist = new ArrayList();
        for (int j = 0;j<DRSlist.size();j++){
            int recipes_id = DRSlist.get(j).getRecipesId();
            String recipes_name = recipesService.getName(recipes_id);
            resultlist.add(recipes_name);
        }
        return resultlist;
    }
    @RequestMapping("selectAllDietRecord")
    public List selectAllDietRecord(){
        List<DietRecord> DRlist = dietRecordService.selectAll();
        int i = 0;
        List<Integer> resultlist = new ArrayList();
        while (i<DRlist.size()){
            int DR_id = DRlist.get(i).getDrId();
            resultlist.add(DR_id);
        }
        return resultlist;
    }

    /*
    * 数据分析图表接口
    * */
    public String DataAnalysis(){

        return "";
    }

}
