package cn.cch.healthy.util;

import cn.cch.healthy.model.Adward;
import cn.cch.healthy.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AdwardUtil {

    @Autowired
    SetMealService setMealService;

    public void fillAdward(List<Adward> adwards)
    {
        for(int i =0;i<adwards.size();i++)
        {
            List recipes = setMealService.selectRecipeId(adwards.get(i).getSmId());

        }
    }
}
