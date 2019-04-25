package cn.cch.healthy.controller;

import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

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

    @Autowired
    FoodService foodService;

    @PostMapping("/getsell")
    public Map getsell() {
        String legend[]={"糖醋排骨","粉蒸肉","京酱肉丝","东坡肉","红烧鸡块","宫保鸡丁","剁椒鱼头"};
        String zao[]={"糖醋排骨","粉蒸肉"};
        String wu[]={"京酱肉丝","东坡肉","红烧鸡块"};
        String wan[]={"红烧鸡块","宫保鸡丁","剁椒鱼头"};
        int data[][]={{112, 223, 111, 154, 190, 30, 10},{12, 234, 125, 452, 190, 330, 410,201},{121, 17, 111, 154, 190, 330, 410},{25, 23, 111, 25, 190, 330, 410},{12, 7, 25, 556, 23, 44, 450},
                {233, 111, 22, 33, 44, 22, 678},{325, 222, 633, 554, 190, 330, 410}};
        int totaldata[][]={{123, 13, 313, 133, 131, 221, 410},{12, 23, 111, 3, 3, 33, 111},{12, 23, 13, 154, 190, 132, 522}};
        Map mymap=new HashMap();
        mymap.put("legend",legend);
        mymap.put("zao",zao);
        mymap.put("wu",wu);
        mymap.put("wan",wan);
        mymap.put("data",data);
        mymap.put("totaldata",totaldata);
        return mymap;
    }


    /*
    * 菜谱配方接口
    * */
    @RequestMapping("deleteFormula")
    public String deleteFormula(@RequestParam("FormulaId") int FormulaId){
        int end = foodFormulaService.DeleteByPrimaryKey(FormulaId);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    /*
    * 搜索某样菜品的配方信息
    * */
    @RequestMapping("selectRecipesFormula")
    public List selectRecipesFormula(@RequestParam("RecipesId") int RecipesId){
        List<FoodFormula> FFlist = foodFormulaService.SelectByRecipesId(RecipesId);
        if (FFlist.size() != 0){
            List resultlist = new ArrayList();
            for (int i = 0;i<FFlist.size();i++){
                Map map = new HashMap();
                int food_id = FFlist.get(i).getFoodId();
                Food food = foodService.selectByPrimaryKey(food_id);

                map.put("formulaId",FFlist.get(i).getFfId());
                map.put("food_name",food.getFoodName());
                map.put("food_number",FFlist.get(i).getFoodNumber());
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
            return resultlist;
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
    public String insertRecipes(@RequestParam("Recipes_name") String Recipes_name){
        Recipes recipes = new Recipes();
        recipes.setRecipesName(Recipes_name);
        int end = recipesService.insert(recipes);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    /*
    * 更新所有菜品的营养信息
    * */
    @RequestMapping("updateRecipesData")
    public void updateRecipesData(){
        List<Recipes> RecipesList = recipesService.Findall();
        for (int i = 0;i<RecipesList.size();i++){
            int Recipes_id = RecipesList.get(i).getRecipesId();

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

            List<FoodFormula> foodFormulaList = foodFormulaService.SelectByRecipesId(Recipes_id);
            for (int n = 0;n<foodFormulaList.size();n++){
                int Foodid = foodFormulaList.get(n).getFoodId();
                double Foodnumber = foodFormulaList.get(n).getFoodNumber();
                Food food = foodService.selectByPrimaryKey(Foodid);

                //计算套餐营养含量
                Food_fat += food.getFoodFat()*9*Foodnumber;
                Food_protein += food.getFoodProtein()*Foodnumber;
                Food_energy += food.getFoodEnergy()*Foodnumber;
                Food_vitamin_A += food.getFoodVitaminA()*Foodnumber;
                Food_vitamin_B_1 += food.getFoodVitaminB1()*Foodnumber;
                Food_vitamin_B_2 += food.getFoodVitaminB2()*Foodnumber;
                Food_vitamin_C += food.getFoodVitaminC()*Foodnumber;
                Food_vitamin_E += food.getFoodVitaminE()*Foodnumber;
                Food_Ca += food.getFoodCa()*Foodnumber;
                Food_Mg += food.getFoodMg()*Foodnumber;
                Food_Fe += food.getFoodFe()*Foodnumber;
                Food_Zn += food.getFoodZn()*Foodnumber;
                Food_cholesterol += food.getFoodCholesterol()*Foodnumber;
            }

            Recipes recipes = new Recipes();
            recipes.setRecipesId(Recipes_id);
            recipes.setRecipesFat(Food_fat/3);
            recipes.setRecipesProtein(Food_protein/3);
            recipes.setRecipesEnergy(Food_energy/3);
            recipes.setRecipesVitaminA(Food_vitamin_A/3);
            recipes.setRecipesVitaminB1(Food_vitamin_B_1/3);
            recipes.setRecipesVitaminB2(Food_vitamin_B_2/3);
            recipes.setRecipesVitaminC(Food_vitamin_C/3);
            recipes.setRecipesVitaminE(Food_vitamin_E/3);
            recipes.setRecipesCa(Food_Ca/3);
            recipes.setRecipesMg(Food_Mg/3);
            recipes.setRecipesFe(Food_Fe/3);
            recipes.setRecipesZn(Food_Zn/3);
            recipes.setRecipesCholesterol(Food_cholesterol/3);
            recipesService.UpdateByPrimaryKeySelective(recipes);
        }
    }

    @RequestMapping("deleteRecipes")
    public String deleteRecipes(@RequestParam("Recipes_id") int Recipes_id){
        int end1 = recipesService.DeleteByPrimaryKey(Recipes_id);
        int end2 = foodFormulaService.DeleteByRecipesId(Recipes_id);
        List<SetMeal> smlist = setMealService.SelectByRecipesid(Recipes_id);
        int i = 0;
        while (i<smlist.size()){
            int smid = smlist.get(i).getSmId();
            int end3 = setmealInfomationService.DeleteByPrimaryKey(smid);
            if (end3 != 1){
                return "失败";
            }
            i++;
        }
        if (end1 == 1 && end2 == 1){
            return "成功";
        }
        return "失败";
    }

    /*
    * 删除文件
    * */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /*
    * 上传文件
    * */
    public static String upload(MultipartFile file,String name) throws IOException {
        if (!file.isEmpty()) {
            String fileName=file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            // 文件上传后的路径
            String filePath = "F:/heathyimg/";
            // 解决中文问题，liunx下中文路径，图片显示问题
            String realfile = filePath + name + suffixName;
            File dest = new File(realfile);

            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }else if(dest.exists()){
                deleteFile(realfile);
            }
            try {
                file.transferTo(dest);
                return name + suffixName;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "fail";
    }

    /*
    *食堂接口
    * */
    @RequestMapping("updateRecipes")
    public String updateRecipes(@RequestParam("Recipes_id") int Recipes_id,
                                @RequestParam("Recipes_name") String Recipes_name,
                                @RequestParam("file") MultipartFile file,
                                @RequestParam("Recipes_price") String Recipes_price,
                                @RequestParam("Recipes_margin") String Recipes_margin) throws IOException {
//        Recipes haverecipes = recipesService.SelectByName(Recipes_name);
        Recipes recipes = recipesService.SelectByPrimaryKey(Recipes_id);
        String Recipes_img = upload(file,String.valueOf(Recipes_id));
        if (Recipes_img.equals("fail")){
            return "文件上传失败";
        }

//        if (haverecipes == null){
//            Recipes recipes = new Recipes();
//            recipes.setRecipesName(Recipes_name);
//            //上传的图片在D盘下的OTA目录下，访问路径如：http://localhost:8080/Img/xxxx.png
//            recipes.setRecipesImg(Recipes_img);
//            recipes.setRecipesPrice(Double.valueOf(Recipes_price));
//            recipes.setRecipesMargin(Double.valueOf(Recipes_margin));
//            recipesService.insert(recipes);
//            return "成功";
//        }
        recipes.setRecipesName(Recipes_name);
        //上传的图片在D盘下的OTA目录下，访问路径如：http://localhost:8080/Img/xxxx.png
        recipes.setRecipesImg("http://118.126.65.227:8080/Img/"+Recipes_img);
        recipes.setRecipesPrice(Double.valueOf(Recipes_price));
        recipes.setRecipesMargin(Double.valueOf(Recipes_margin));
        int end = recipesService.UpdateByPrimaryKeySelective(recipes);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    //模糊查询
    @RequestMapping("VagueSelectRecipes")
    public List<Recipes> VagueSelectRecipes(@RequestParam("Recipes_name") String Recipes_name){
        List<Recipes> recipesList = recipesService.VagueSelectRecipes(Recipes_name);
        return recipesList;
    }

    @RequestMapping("selectAllRecipes")
    public List<Recipes> selectAllRecipes(){
        List<Recipes> recipes_list = recipesService.Findall();
        return recipes_list;
    }

    /*
    * 食堂饮食记录接口
    * */

    /*
    *获得特价和推荐菜品
    * */
    @RequestMapping("GetWellRecipes")
    public List<Recipes> GetWellRecipes(@RequestParam("type") String type){
        List<Recipes> resultList = null;
        if (type.equals("新品特价")){
            resultList = recipesService.SelectRecipesByType("特价");
        }else if (type.equals("食堂推荐")){
            resultList = recipesService.SelectRecipesByType("推荐");
        }
        return resultList;
    }

    /*
    * 获得热销菜品
    * */
    @RequestMapping("GetSellWellRecipes")
    public List<HashMap> GetSellWellRecipes(){
        List<DietRecord> DRlist = dietRecordService.SelectMonthDiet();
        List<Map> list = new ArrayList<>();
        int i = 0;
        while (i < DRlist.size()){
            int DR_id = DRlist.get(i).getDrId();
            List<Integer> recipesIdList = dietRecordService.SelectByDRid(DR_id);
            int j = 0;
            while (j < recipesIdList.size()){
                Map<Integer,Integer> map = new HashMap<>();
                map.put(recipesIdList.get(j),1);
                list.add(map);
                j++;
            }
            i++;
        }

        Map m = mapCombine(list);
        Map<Integer, Integer> finalmap = new TreeMap<Integer, Integer>();
        Set<Integer> set = m.keySet(); //取出所有的key值
        for (Integer key:set) {
            List newList = new ArrayList<>();
            newList = (List) m.get(key);
            int lenght = newList.size();
            finalmap.put(key,lenght);
        }
        //这里将map.entrySet()转换成list
        List<Map.Entry<Integer,Integer>> list1 = new ArrayList<Map.Entry<Integer,Integer>>(finalmap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list1,new Comparator<Map.Entry<Integer,Integer>>() {
            //降序排序
            public int compare(Entry<Integer, Integer> o1,
                               Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        int number = 1;
        List<HashMap> resultList = new ArrayList<>();
        //取前十热销
        for(Map.Entry<Integer,Integer> mapping:list1){
//            System.out.println(mapping.getKey()+":"+mapping.getValue());
            if (number>10){
                break;
            }
            int recipes_id = mapping.getKey();
            if (recipes_id == 44 || recipes_id == 27 ||recipes_id == 30 ||recipes_id == 31 ||recipes_id == 43 ||
                    recipes_id == 45 ||recipes_id == 39)
                continue;
            HashMap Wellmap = new HashMap();
            Recipes recipes = recipesService.SelectByPrimaryKey(recipes_id);
            Wellmap.put("菜品信息",recipes);
            Wellmap.put("菜品销量",mapping.getValue());
            resultList.add(Wellmap);
            number++;
        }
        return resultList;
    }

    public static Map mapCombine(List<Map> list) {
        Map<Object, List> map = new HashMap<>();
        for (Map m : list) {
            Iterator<Object> it = m.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                if (!map.containsKey(key)) {
                    List newList = new ArrayList<>();
                    newList.add(m.get(key));
                    map.put(key, newList);
                } else {
                    map.get(key).add(m.get(key));
                }
            }
        }
        return map;
    }

    /*
    * 显示某次交易
    * 菜品列表
    * */
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

    /*
    *
    * */
    @RequestMapping("selectAllDietRecord")
    public List selectAllDietRecord(){
        List<DietRecord> DRlist = dietRecordService.selectAll();
        int i = 0;
        List<Integer> resultlist = new ArrayList();
        while (i<DRlist.size()){
            int DR_id = DRlist.get(i).getDrId();
            resultlist.add(DR_id);
            i++;
        }
        return resultlist;
    }

    /*
    * 数据分析图表接口
    * */
    public String DataAnalysis(){

        return "";
    }

    /*
    * 数据设置器
    */

    /*
    * 设置菜品余量
    * */
    @RequestMapping("MakeMargin")
    public String MakeMargin(){
        List<Recipes> recipesList = recipesService.Findall();
        int i = 0;
        while (i<recipesList.size()){
            double margin = (double) getRandom(30,70);
            Recipes recipes = recipesList.get(i);
            recipes.setRecipesMargin(margin);
            recipesService.UpdateByPrimaryKeySelective(recipes);
            i++;
        }
        return "成功";
    }

    /*
     *设置销售
     * */
    @RequestMapping("MakeSale")
    public String MakeSale(@RequestParam("RecipesArray") int[] RecipesArray,
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
                dietRecordService.insertsub(DRS);

                Recipes recipes = recipesService.SelectByPrimaryKey(RecipesArray[i]);
                if (recipes.getRecipesMargin()>0){
                    recipes.setRecipesMargin(recipes.getRecipesMargin()-1);
                    recipesService.UpdateByPrimaryKeySelective(recipes);
                }else{
                    return recipes.getRecipesName()+"已经买完了";
                }
                i++;
            }
            return "成功";
        }
        return "失败";
    }

    public static int getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    /*
    * 设置菜品配方
    * */
    @RequestMapping("MakeFormula")
    public String MakeFormula(@RequestParam("recipes_name") String recipes_name,@RequestParam("food_name") String food_name,
                              @RequestParam("food_number") String food_number){
        int recipes_id = recipesService.GetId(recipes_name);
        List<FoodFormula> fflist = foodFormulaService.SelectByRecipesId(recipes_id);
        int food_id = foodService.GetId(food_name);
        int i = 0;
        while (i<fflist.size()){
            if (food_id == fflist.get(i).getFoodId()){
                fflist.get(i).setFoodNumber(Double.valueOf(food_number));
                foodFormulaService.UpdateByPrimaryKeySelective(fflist.get(i));
                return "成功";
            }
            i++;
        }

        FoodFormula ff = new FoodFormula();
        ff.setFoodNumber(Double.valueOf(food_number));
        ff.setFoodId(food_id);
        ff.setRecipesId(recipes_id);
        int end = foodFormulaService.insert(ff);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    /*
    * 设置食材信息
    * */
    @RequestMapping("Makefood")
    public String Makefood(@RequestParam("Food_name") String Food_name,@RequestParam("Food_fat") String Food_fat,
                           @RequestParam("Food_protein") String Food_protein,@RequestParam("Food_energy") String Food_energy,
                           @RequestParam("Food_vitamin_A") String Food_vitamin_A,@RequestParam("Food_vitamin_B_1") String Food_vitamin_B_1,
                           @RequestParam("Food_vitamin_B_2") String Food_vitamin_B_2,@RequestParam("Food_vitamin_C") int Food_vitamin_C,
                           @RequestParam("Food_vitamin_E") String Food_vitamin_E,@RequestParam("Food_Ca") String Food_Ca,
                           @RequestParam("Food_Mg") String Food_Mg,@RequestParam("Food_Fe") String Food_Fe,
                           @RequestParam("Food_Zn") String Food_Zn,@RequestParam("Food_cholesterol") String Food_cholesterol){
        Food havefood = foodService.SelectByName(Food_name);
        Food food = new Food();
        if (havefood == null){
            food.setFoodFat(Double.valueOf(Food_fat));
            food.setFoodProtein(Double.valueOf(Food_protein));
            food.setFoodEnergy(Double.valueOf(Food_energy));
            food.setFoodVitaminA(Double.valueOf(Food_vitamin_A));
            food.setFoodVitaminB1(Double.valueOf(Food_vitamin_B_1));
            food.setFoodVitaminB2(Double.valueOf(Food_vitamin_B_2));
            food.setFoodVitaminC(Food_vitamin_C);
            food.setFoodVitaminE(Double.valueOf(Food_vitamin_E));
            food.setFoodCa(Double.valueOf(Food_Ca));
            food.setFoodMg(Double.valueOf(Food_Mg));
            food.setFoodFe(Double.valueOf(Food_Fe));
            food.setFoodZn(Double.valueOf(Food_Zn));
            food.setFoodCholesterol(Double.valueOf(Food_cholesterol));
            int end = foodService.insert(food);
            if (end == 1){
                return "成功";
            }
            return "失败";
        }else{
            food.setFoodId(havefood.getFoodId());
            food.setFoodFat(Double.valueOf(Food_fat));
            food.setFoodProtein(Double.valueOf(Food_protein));
            food.setFoodEnergy(Double.valueOf(Food_energy));
            food.setFoodVitaminA(Double.valueOf(Food_vitamin_A));
            food.setFoodVitaminB1(Double.valueOf(Food_vitamin_B_1));
            food.setFoodVitaminB2(Double.valueOf(Food_vitamin_B_2));
            food.setFoodVitaminC(Food_vitamin_C);
            food.setFoodVitaminE(Double.valueOf(Food_vitamin_E));
            food.setFoodCa(Double.valueOf(Food_Ca));
            food.setFoodMg(Double.valueOf(Food_Mg));
            food.setFoodFe(Double.valueOf(Food_Fe));
            food.setFoodZn(Double.valueOf(Food_Zn));
            food.setFoodCholesterol(Double.valueOf(Food_cholesterol));
            int end = foodService.UpdateByPrimaryKeySelective(food);
            if (end == 1){
                return "成功";
            }else{
                return "失败";
            }
        }
    }

    @RequestMapping("deletefood")
    public String deletefood(@RequestParam("Food_id") int Food_id){
        int end = foodService.DeleteByPrimaryKey(Food_id);
        if (end == 1){
            return "成功";
        }else{
            return "失败";
        }
    }

    @RequestMapping("ceshi")
    public void ceshi(){
        Food food_id = foodService.SelectByName("糖");
        if (food_id == null){
            food_id.setFoodCa(1.1);
            System.out.println(food_id.getFoodCa());
        }
        System.out.println(food_id);
    }

}
