package cn.cch.healthy.util;

import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetRemoveSetmeal {

    @Autowired
    UserIllnessService userIllnessService;

    @Autowired
    FoodillnessService foodillnessService;

    @Autowired
    RecipesIllnessService recipesIllnessService;

    @Autowired
    FoodFormulaService foodFormulaService;

    @Autowired
    SetMealService setMealService;

    public GetRemoveSetmeal(){

    }

    public static GetRemoveSetmeal getRemoveSetmeal;

    /*
    * 传进参数：User_id
    * 返回参数：ArrayList SetmealIdlist
    *
    * 功能：获取用户不能食用的套餐集合
    * */

    public List GetRemoveSetmeal(List<UserIllness> UINlist){

        System.out.println(UINlist);
        if (UINlist.size() != 0||UINlist!=null){
            ArrayList<Integer> RecipesIdlist = new ArrayList<>();
            for (int i = 0;i<UINlist.size();i++){
                int ill_id = UINlist.get(i).getIllId();
                List<RecipesIllness> RIlist = getRemoveSetmeal.recipesIllnessService.SelectByillId(ill_id);
                if (RIlist.size() != 0){
                    for (int n = 0;n<RIlist.size();n++){
                        int Recipes_id = RIlist.get(n).getRecipesId();
                        RecipesIdlist.add(Recipes_id);
                    }
                }

                List<FoodIllness> FIlist = getRemoveSetmeal.foodillnessService.SelectByillId(ill_id);
                if (FIlist.size() != 0){
                    for (int m = 0;m< FIlist.size();m++){
                        int food_id = FIlist.get(m).getFoodId();
                        List<FoodFormula> FFlist = getRemoveSetmeal.foodFormulaService.SelectByFoodid(food_id);
                        if (FFlist.size() != 0){
                            for (int p = 0;p<FFlist.size();p++){
                                int Recipes_id = FFlist.get(p).getRecipesId();
                                RecipesIdlist.add(Recipes_id);
                            }
                        }
                    }
                }
            }

            if (RecipesIdlist.size() != 0){
                for (int i = 0; i < RecipesIdlist.size() - 1; i++) {
                    for (int j = i + 1; j < RecipesIdlist.size(); j++) {
                        if (RecipesIdlist.get(i) == RecipesIdlist.get(j)) {
                            RecipesIdlist.remove(Integer.valueOf(RecipesIdlist.get(j)));
                        }
                    }
                }

                ArrayList<Integer> SetmealIdlist = new ArrayList<Integer>();
                for (int i = 0;i<RecipesIdlist.size();i++){
                    List<SetMeal> SMlist = getRemoveSetmeal.setMealService.SelectByRecipesid(RecipesIdlist.get(i));
                    if (SMlist.size() != 0){
                        for (int j = 0;j<SMlist.size();j++){
                            SetmealIdlist.add(SMlist.get(j).getSmId());
                        }
                    }
                }

                for (int i = 0; i < SetmealIdlist.size() - 1; i++) {
                    for (int j = i + 1; j < SetmealIdlist.size(); j++) {
                        if (SetmealIdlist.get(i) == SetmealIdlist.get(j)) {
                            SetmealIdlist.remove(Integer.valueOf(SetmealIdlist.get(j)));
                        }
                    }
                }

                return SetmealIdlist;
            }else{
                List<Integer> list = new ArrayList<Integer>();
                return list;
            }
        }else{
            List<Integer> list = new ArrayList<Integer>();
            return list;
        }


    }

    @PostConstruct
    public void init() {
        getRemoveSetmeal = this;
        getRemoveSetmeal.userIllnessService = this.userIllnessService;
        getRemoveSetmeal.foodillnessService = this.foodillnessService;
        getRemoveSetmeal.recipesIllnessService = this.recipesIllnessService;
        getRemoveSetmeal.foodFormulaService = this.foodFormulaService;
        getRemoveSetmeal.setMealService = this.setMealService;
    }
}
