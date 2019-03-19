package cn.cch.healthy.util;

import cn.cch.healthy.model.Adward;
import cn.cch.healthy.service.DietRecordService;
import cn.cch.healthy.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class AdwardUtil {


    public List<Adward> balanceWeight(List<Map> records,List<Adward> adwards) throws Exception
    {

        if (records.size()==0||records==null||adwards.size()==0||adwards==null)
        {
            System.out.println("没有任何记录");
            return adwards;
        }
        for(int i =0;i<adwards.size();i++)
        {
            List<Integer> recipes = adwards.get(i).getRecipeList();
            if (recipes.size()==0)
            {
                System.out.println("没有任何记录");
                return adwards;
            }
            for(int j=0;j<recipes.size();j++)
            {
                for (int z=0;z<records.size();z++)
                {
                    if (recipes.get(j)==records.get(z).get("recipeId"))
                    {
                        double deWeight = Double.parseDouble(records.get(z).get("deWeight").toString());
                        if((adwards.get(i).getWeight()-deWeight)>=0)
                            adwards.get(i).setWeight(adwards.get(i).getWeight()-deWeight);
                        else
                            adwards.get(i).setWeight(0);
                       // System.out.println("发现"+recipes.get(j)+"号菜最近吃过"+adwards.get(i).getSmId()+"号套餐减少了"+deWeight+"点权重");
                    }
                }
            }
        }
        return adwards;
    }
}
