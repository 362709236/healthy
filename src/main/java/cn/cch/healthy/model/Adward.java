package cn.cch.healthy.model;

import cn.cch.healthy.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Adward {
    int smId;
    double weight;
    List recipeList;
    public Adward(int smId,int weight)
    {
        this.smId=smId;
        this.weight=weight;
    }
    public Adward(int smId)
    {
        this.smId=smId;
        this.weight=1;
    }
    public int getSmId() {
        return smId;
    }

    public void setSmId(int smId) {
        this.smId = smId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List recipeList) {
        this.recipeList = recipeList;
    }
}
