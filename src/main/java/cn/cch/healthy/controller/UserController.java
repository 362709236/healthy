package cn.cch.healthy.controller;

import cn.cch.healthy.model.*;
import cn.cch.healthy.service.*;
import cn.cch.healthy.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserinfoService userinfoService;

    @Autowired
    CompareImage testservice;

    @Autowired
    IllnessService illnessService;

    @Autowired
    UserIllnessService userIllnessService;

    @Autowired
    OccupationService occupationService;

    @Autowired
    DietRecordService dietRecordService;

    @Autowired
    FoodFormulaService foodFormulaService;

    @Autowired
    FoodService foodService;

    @Autowired
    RecommendUtil recommendUtil;

    @Autowired
    InterestService interestService;

    @Autowired
    RecipesService recipesService;

    @Autowired
    StandardIntakeService standardIntakeService;

    @Autowired
    UserConnentService userConnentService;

    @RequestMapping("uploadPic")
    public String SetUserFace(MultipartFile file,@RequestParam("openid") String openid) throws IOException {
        if (file == null || "".equals(file.getOriginalFilename())) {
            return "上传的照片为空";
        } else {
            if (file.getSize() > 5*1024*1024){
                return "您上传的文件过大，请重试";
            }
            String[] strArray = file.getOriginalFilename().split("\\.");
            String Filetype = strArray[1];
            //bmp,jpg,png
            if (!(Filetype.equals("bmp") || Filetype.equals("jpg") || Filetype.equals("png"))){
                return "您上传的文件格式不对，请转换为bmp,jpg,png";
            }
            String str = FaceUtil.check(file.getBytes());
            try {
                JSONObject json = new JSONObject(str);
                JSONArray faceArray = json.getJSONArray("faces");
                String faces = faceArray.toString();
                if ("[]".equals(faces)) {
                    return "对不起，您上传的不是用户头像或者照片质量不达标，请重新上传！";
                }
                JSONObject josnToken = new JSONObject(faces.substring(1, faces.length() - 1));
                String token = josnToken.getString("face_token");
                System.out.println("token"+token);
                FaceUtil.save(token);
                //保存用户的facetoken
                Userinfo user = userinfoService.SelectByOpenid(openid);
                user.setUserFaceToken(token);
                userinfoService.UpdateByPrimaryKeySelective(user);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "系统繁忙，请稍后重试！";
            }
        }
        return "成功";
    }

    //比对用户
    @RequestMapping("/compare")
    public String CompareFace(String file) throws Exception {
        System.out.println(file);
        List<String> faceList=testservice.test2(file);

        Map map = new HashMap();
        List<Userinfo> userList=userinfoService.SelectAll();  //获取所有用户
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;

        if (faceList.size()==0||userList.size()==0)
            return "没有检测到人脸或者无用户face_token";
        for(int i=0;i<faceList.size();i++){
            for(int j=0;j<userList.size();j++){
                //将拍摄图片所得facetoken与用户的facetoken进行比对
                if (userList.get(j).getUserFaceToken()==null){
                    continue;
                }
                //推送内容
                Userinfo user = userList.get(j);
                Date Recognize_time1 = user.getUserRecognize();
                Date Recognize_time2 = new Date();
                if (Recognize_time1 != null){
                    // 获得两个时间的毫秒时间差异
                    long diff = Math.abs(Recognize_time1.getTime() - Recognize_time2.getTime());
                    // 计算差多少天
                    long day = diff / nd;
                    // 计算差多少小时
                    long hour = diff % nd / nh;
                    if (day == 0){
                        if (hour < 1){
                            return "还不到一个小时,无法进行推送";
                        }
                    }
                }
                //更新记录时间
                user.setUserRecognize(new Date());
                boolean isExit=FaceUtil.compare(userList.get(j).getUserFaceToken(),faceList.get(i));
                if(isExit){
                    userinfoService.UpdateByPrimaryKeySelective(user);
                    //触发推送
                    map=recommendUtil.recommend_score(userList.get(j).getUserId(),50);
                    map.put("openid",user.getUserOpenid());
                    JSONObject JSONmap = new JSONObject(map);
                    HttpTest.appadd(JSONmap);
                }
            }
        }
        return "成功";
    }

    @RequestMapping("GetUserData")
    public HashMap GetUserData(@RequestParam("openid") String openid){
        System.out.println("进入获取信息控制器");
        Userinfo user= userinfoService.SelectByOpenid(openid);
        int user_id = user.getUserId();
        List<Integer> UIlist = userIllnessService.SelectByUserid(user_id);
        List<String> IllnameList = new ArrayList<String>();
        if (UIlist.size() != 0){
            for (int i = 0;i<UIlist.size();i++){
                int ill_id = UIlist.get(i);
                Illness ill = illnessService.SelectByPrimaryKey(ill_id);
                String ill_name = ill.getIllName();
                IllnameList.add(ill_name);
            }
        }

        List<String> InterestNameList = new ArrayList<String>();
        List<Integer> UINlist = interestService.SelectByUserId(user_id);
        if (UINlist.size() != 0){
            for (int i = 0;i<UINlist.size();i++){
                Interest interest = interestService.SelectByPrimaryKey(UINlist.get(i));
                String InterestName = interest.getInterestName();
                InterestNameList.add(InterestName);
            }
        }

        HashMap<String, Object> map=new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (user.getUserBirthday() != null){
            String birthday = sdf.format(user.getUserBirthday());
            map.put("userBirthday",birthday);
        }

        map.put("userSex",user.getUserSex());
        map.put("userHeight",user.getUserHeight());
        map.put("userWeight",user.getUserWeight());
        map.put("userCcupation",user.getUserCcupation());
        map.put("userType",user.getUserType());
        map.put("userNumber",user.getUserNumber());
        map.put("userInterests",InterestNameList);
        map.put("userIllness",IllnameList);

        return map;
    }

    @RequestMapping("updateUserInfo")
    public String UpdateUserinfo(@RequestParam("openid") String openid,
                                 @RequestParam("usersex") String sex,@RequestParam("userborn") String birthday,
                                 @RequestParam("userheight") String height_str,@RequestParam("userweight") String weight_str,
                                 @RequestParam("occupation") String occupation,@RequestParam("userills")String Illness_Str,
                                 @RequestParam("userinterests") String Interest_Str,@RequestParam("usernumber") String User_number,
                                 @RequestParam("usertype") String User_type) throws ParseException {
        Userinfo user = userinfoService.SelectByOpenid(openid);
        System.out.println("进入修改信息控制器");

        if(!(birthday == null || birthday.equals(""))){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthday_date = sdf.parse(birthday);

            int user_year = Integer.parseInt(birthday.substring(0,4));
            int year = date.getYear()+1900;
            int age = year - user_year + 1;
            user.setUserAge(age);
            user.setUserBirthday(birthday_date);
        }else{
            user.setUserAge(null);
            user.setUserBirthday(null);
        }

        if (User_number.equals(""))
            user.setUserNumber(null);
        user.setUserNumber(User_number);
        if (sex.equals(""))
            user.setUserSex(null);
        user.setUserSex(sex);
        if (User_type.equals(""))
            user.setUserType(null);
        user.setUserType(User_type);

        if (!(height_str == null || height_str.equals(""))){
            double height = Double.valueOf(height_str);
            user.setUserHeight(height);
        }else{
            user.setUserHeight(null);
        }
        if (!(weight_str == null || weight_str.equals(""))){
            double weight = Double.valueOf(weight_str);
            user.setUserWeight(weight);
        }else{
            user.setUserWeight(null);
        }

        if (occupation.equals(""))
            user.setUserCcupation(null);
        user.setUserCcupation(occupation);

        int user_id = user.getUserId();

        userConnentService.ConnectIllness(user_id,Illness_Str);
        userConnentService.ConnectInterest(user_id,Interest_Str);

        int end = userinfoService.updateByPrimaryKey(user);

        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping(value = "insertUser",method = RequestMethod.POST)
    public int insertUser(@RequestParam("openid") String openid) throws ParseException {
        Userinfo user = userinfoService.SelectByOpenid(openid);
        if (user == null)
        {
            Calendar calendar = Calendar.getInstance();
            /* HOUR_OF_DAY 指示一天中的小时 */
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 2);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Userinfo new_user = new Userinfo();
            new_user.setUserOpenid(openid);
            String dd = df.format(calendar.getTime());
            Date date = df.parse(dd);
            new_user.setUserRecognize(date);
            userinfoService.insert(new_user);
            return 0;
        }else
        {
            return 1;
        }
    }

    @RequestMapping("updateUserInfo1")
    public String updateUserInfo1(@RequestParam("openid") String openid,@RequestParam("type") String type,
                                  @RequestParam("numberID") String numberID){
        Userinfo user = userinfoService.SelectByOpenid(openid);
        if (user == null)
        {
            /*Userinfo new_user = new Userinfo();
            new_user.setUserOpenid(openid);
            userinfoService.insert(new_user);*/
            return "用户不存在";
       }
        user.setUserType(type);
        user.setUserNumber(numberID);
        int end = userinfoService.UpdateByPrimaryKeySelective(user);
        if (end == 1){
            return "成功";
        }
        return "失败";
    }


    @RequestMapping("updateUserInfo2")
    public String updateUserInfo2(@RequestParam("openid") String openid,@RequestParam("height") String height,
                                  @RequestParam("weight") String weight,@RequestParam("sex") String sex){
        Double dou_height = Double.valueOf(height);
        Double dou_weight = Double.valueOf(weight);

        Userinfo user = userinfoService.SelectByOpenid(openid);
        user.setUserWeight(dou_weight);
        user.setUserHeight(dou_height);
        user.setUserSex(sex);
        int end = userinfoService.UpdateByPrimaryKeySelective(user);

        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("updateUserInfo3")
    public String updateUserInfo3(@RequestParam("openid") String openid,@RequestParam("userborn") String birthday,
                                  @RequestParam("occupation") String occupation) throws ParseException {
        Userinfo user = userinfoService.SelectByOpenid(openid);
        if(!(birthday == null || birthday.equals(""))){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthday_date = sdf.parse(birthday);

            Calendar c = Calendar.getInstance();
            c.setTime(birthday_date);
            c.add(Calendar.DAY_OF_MONTH, 1);
            birthday_date = c.getTime();

            int user_year = Integer.parseInt(birthday.substring(0,4));
            int year = date.getYear()+1900;
            int age = year - user_year + 1;
            user.setUserAge(age);
            user.setUserBirthday(birthday_date);
        }
        user.setUserCcupation(occupation);

        int end = userinfoService.UpdateByPrimaryKeySelective(user);

        if (end == 1){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("updateUserInfo4")
    public void updateUserInfo4(@RequestParam("interestStr") String interestStr,
                                  @RequestParam("openid") String openid){
        Userinfo user = userinfoService.SelectByOpenid(openid);
        int user_id = user.getUserId();
        userConnentService.ConnectInterest(user_id,interestStr);
    }

    @RequestMapping("updateUserInfo5")
    public void updateUserInfo5(@RequestParam("openid") String openid,@RequestParam("userills")String Illness_Str){
        Userinfo user = userinfoService.SelectByOpenid(openid);
        int user_id = user.getUserId();
        userConnentService.ConnectIllness(user_id,Illness_Str);
    }

    //获得用户饮食记录表
    @RequestMapping("GetUserWeekdietRecord")
    public List<HashMap> GetUserWeekdietRecord(@RequestParam("openid") String openid){
        Userinfo user = userinfoService.SelectByOpenid(openid);
        int user_id = user.getUserId();
        List<DietRecord> DRlist = dietRecordService.SelectUserWeekDiet(user_id);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<HashMap> resultList = new ArrayList<>();
        int i = 0;
        while (i<DRlist.size()){
            HashMap map = new HashMap();
            String datestr = sdf.format(DRlist.get(i).getDrDate());
            map.put("diettime",datestr);
            int DR_id = DRlist.get(i).getDrId();
            List<Integer> DRSlist = dietRecordService.SelectByDRid(DR_id);
            List<Recipes> recipesList = new ArrayList<Recipes>();
            int j = 0;
            while (j < DRSlist.size()){
                Recipes recipes = recipesService.SelectByPrimaryKey(DRSlist.get(j));
                recipesList.add(recipes);
                j++;
            }
            map.put("recipesList",recipesList);
            resultList.add(map);
            i++;
        }
        return resultList;
    }

    //获得用户饮食记录中菜品信息
    @RequestMapping("GetUserdietRecordSub")
    public List GetUserdietRecordSub(@RequestParam("DRid") int DRid){
        List<Integer> DRSlist = dietRecordService.SelectByDRid(DRid);
        List<Recipes> resultList = new ArrayList<Recipes>();
        int i = 0;
        while (i<DRSlist.size()){
            Recipes recipes = recipesService.SelectByPrimaryKey(DRSlist.get(i));
            resultList.add(recipes);
            i++;
        }
        return resultList;
    }

    //判断用户是否存在数据库
    @RequestMapping("JudgeUser")
    public String JudgeUser(@RequestParam("openid") String openid){
        Userinfo user = userinfoService.SelectByOpenid(openid);
        if (user == null){
            return "不存在该用户";
        }
        return "存在该用户";
    }

    //用户周报信息
    /*
     * 200：成功
     * 404：没有找到用户
     * 400：没有饮食记录
     * 401：用户信息不全
     * */
    @RequestMapping("GetUserWeekly")
    public HashMap GetUserWeekly(@RequestParam("openid") String openid){
        HashMap FinalMap = new HashMap();
        Userinfo user = userinfoService.SelectByOpenid(openid);
        if (user == null){
            FinalMap.put("错误码",404);
            return FinalMap;
        }
        int user_id = user.getUserId();
        Date now_date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date ThisWeeklyDate;
            Date LastWeeklyDate;
            if (now_date.compareTo(getThisWeekMonday(now_date)) == -1){
                ThisWeeklyDate = geLastWeekMonday(now_date);
                LastWeeklyDate = geLLastWeekMonday(now_date);
            }else{
                ThisWeeklyDate = getThisWeekMonday(now_date);
                LastWeeklyDate = geLastWeekMonday(now_date);
            }
            String ThisWeeklyDate_Str = sdf.format(ThisWeeklyDate) + " 08:00:00";
            String LastWeeklyDate_Str = sdf.format(LastWeeklyDate) + " 08:00:00";

            if (user.getUserAge() == null || user.getUserCcupation() == null || user.getUserSex() == null){
                FinalMap.put("错误码",401);
                return FinalMap;
            }

            StandardIntake intake = standardIntakeService.getStandardIntake(user);

            //用户营养
            //能量
            double All_Energy = 0;
            //蛋白质
            double All_portein = 0;
            //脂肪
            double All_fat = 0;
            //维生素A
            double All_vitamin_A = 0;
            //维生素B1
            double All_vitamin_B_1 = 0;
            //维生素B2
            double All_vitamin_B_2 = 0;
            //维生素C
            double All_vitamin_C = 0;

            //标准营养
            //能量
            double All_Energy2 = 0;
            //蛋白质
            double All_portein2 = 0;
            //脂肪
            double All_fat2 = 0;
            //维生素A
            double All_vitamin_A2 = 0;
            //维生素B1
            double All_vitamin_B_12 = 0;
            //维生素B2
            double All_vitamin_B_22 = 0;
            //维生素C
            double All_vitamin_C2 = 0;

            //能量
            double Standard_Energy;
            if (user.getUserSex().equals("女")){
                Standard_Energy = ((655+(9.6*user.getUserWeight())+(1.8*user.getUserHeight())-(4.7*user.getUserAge()))*1.55);
            }else{
                Standard_Energy = ((66+(13.7*user.getUserWeight())+(5*user.getUserHeight())-(6.8*user.getUserAge()))*1.55);
            }
            //蛋白质
            double Standard_portein = intake.getSiPortein();
            //脂肪
            double Standard_fat = Standard_Energy/30;
            //维生素A
            double Standard_vitamin_A = (double)(intake.getSiVitaminA() / 12);
            //维生素B1
            double Standard_vitamin_B_1 = intake.getSiVitaminB1();
            //维生素B2
            double Standard_vitamin_B_2 = intake.getSiVitaminB2();
            //维生素C
            double Standard_vitamin_C = intake.getSiVitaminC();

            List<DietRecord> DRlist = dietRecordService.GetUserWeeklySQL(LastWeeklyDate_Str,ThisWeeklyDate_Str,user_id);

            if (DRlist.size() == 0){
                FinalMap.put("错误码",400);
                return FinalMap;
            }
            int i = 0;
            List<Map> Combinelist1 = new ArrayList<>();
            List<Map> Combinelist2 = new ArrayList<>();
            while (i<DRlist.size()){
                List<Integer> recipesList = dietRecordService.SelectByDRid(DRlist.get(i).getDrId());
                int time = DRlist.get(i).getDrTime();
                double number = 0;
                switch (time){
                    case 1:
                        number = 0.3;
                        break;
                    case 2:
                        number = 0.4;
                        break;
                    case 3:
                        number = 0.3;
                        break;
                }
                //能量
                 All_Energy2 += Standard_Energy * number;
                //蛋白质
                 All_portein2 += Standard_portein * number;
                //脂肪
                 All_fat2 += Standard_fat * number;
                //维生素A
                 All_vitamin_A2 += Standard_vitamin_A * number;
                //维生素B1
                 All_vitamin_B_12 += Standard_vitamin_B_1 * number;
                //维生素B2
                 All_vitamin_B_22 += Standard_vitamin_B_2 * number;
                //维生素C
                 All_vitamin_C2 += Standard_vitamin_C * number;

                 int j = 0;
                while (j < recipesList.size()){
                    int recipes_id = recipesList.get(j);
                    Recipes recipes = recipesService.SelectByPrimaryKey(recipes_id);
                    String recipes_type = recipes.getRecipesType();

                    //素菜
                    Map<Integer,Integer> Combinemap1 = new HashMap<>();
                    //荤菜
                    Map<Integer,Integer> Combinemap2 = new HashMap<>();

                    if (recipes_type.indexOf("素菜") != -1){
                        Combinemap1.put(recipes_id,1);
                        Combinelist1.add(Combinemap1);
                    }
                    if (recipes_type.indexOf("荤菜") != -1){
                        Combinemap2.put(recipes_id,1);
                        Combinelist2.add(Combinemap2);
                    }

                    //能量
                     All_Energy += recipes.getRecipesEnergy();
                    //蛋白质
                     All_portein += recipes.getRecipesProtein();
                    //脂肪
                     All_fat += recipes.getRecipesFat();
                    //维生素A
                     All_vitamin_A += recipes.getRecipesVitaminA();
                    //维生素B1
                     All_vitamin_B_1 += recipes.getRecipesVitaminB1();
                    //维生素B2
                     All_vitamin_B_2 += recipes.getRecipesVitaminB2();
                    //维生素C
//                    System.out.println(recipes.getRecipesVitaminC());
                     All_vitamin_C += recipes.getRecipesVitaminC();
//                     System.out.println("All_vitamin_C："+All_vitamin_C);
                    j++;
                }
                i++;
            }

            System.out.println("用户营养值"+"All_Energy："+All_Energy);
            System.out.println("用户营养值"+"All_portein："+All_Energy);
            System.out.println("用户营养值"+"All_fat："+All_fat);
            System.out.println("用户营养值"+"All_vitamin_A："+All_vitamin_A);
            System.out.println("用户营养值"+"All_vitamin_B_1："+All_vitamin_B_1);
            System.out.println("用户营养值"+"All_vitamin_B_2："+All_vitamin_B_2);
            System.out.println("用户营养值"+"All_vitamin_C："+All_vitamin_C);

            System.out.println("标准营养值"+"All_Energy2："+All_Energy2);
            System.out.println("标准营养值"+"All_portein2："+All_Energy2);
            System.out.println("标准营养值"+"All_fat2："+All_fat2);
            System.out.println("标准营养值"+"All_vitamin_A2："+All_vitamin_A2);
            System.out.println("标准营养值"+"All_vitamin_B_12："+All_vitamin_B_12);
            System.out.println("标准营养值"+"All_vitamin_B_22："+All_vitamin_B_22);
            System.out.println("标准营养值"+"All_vitamin_C2："+All_vitamin_C2);


            List<Map.Entry<Integer,Integer>> list1 = getEntryMap(Combinelist1);
            List<Map.Entry<Integer,Integer>> list2 = getEntryMap(Combinelist2);

            HashMap VMMap = new HashMap();
            int number = 1;
            //素菜
            List<HashMap> VegetableList = new ArrayList<>();
            Map<String,String> VegetableMap = new HashMap();
            //取前五素菜菜
            for(Map.Entry<Integer,Integer> mapping:list1){
                if (number>5){
                    break;
                }
                int recipes_id = mapping.getKey();
                int recipes_number = mapping.getValue();
                String recipes_name = recipesService.getName(recipes_id);
                HashMap map_ = new HashMap();
                map_.put("recipes_name",recipes_name);
                map_.put("recipes_number",recipes_number);
                VegetableList.add(map_);

                if (number == 1){
                    VegetableMap.put("recipes_id",String.valueOf(recipes_id));
                    VegetableMap.put("recipes_name",recipes_name);
                    VegetableMap.put("recipes_number",String.valueOf(recipes_number));
                }

                number++;
            }

            number = 1;
            //荤菜
            List<HashMap> MeatList = new ArrayList<>();
            Map<String,String> MeatMap = new HashMap();

            //取前五荤菜
            for(Map.Entry<Integer,Integer> mapping:list2){
                if (number>5){
                    break;
                }
                int recipes_id = mapping.getKey();
                int recipes_number = mapping.getValue();
                String recipes_name = recipesService.getName(recipes_id);
                HashMap map_ = new HashMap();
                map_.put("recipes_name",recipes_name);
                map_.put("recipes_number",recipes_number);
                MeatList.add(map_);

                if (number == 1){
                    MeatMap.put("recipes_id",String.valueOf(recipes_id));
                    MeatMap.put("recipes_name",recipes_name);
                    MeatMap.put("recipes_number",String.valueOf(recipes_number));
                }

                number++;
            }

            List<String> LikeList1 = new ArrayList<>();
            List<String> LikeList2 = new ArrayList<>();

            int VMchoose = 0;

            if (VegetableMap.size() == 0 && MeatMap.size() == 0){
                VMchoose = 0;
            }else if (VegetableMap.size() == 0){
                VMchoose = 2;
            }else if (MeatMap.size() == 0){
                VMchoose = 1;
            }else if (Integer.parseInt(MeatMap.get("recipes_number")) > Integer.parseInt(VegetableMap.get("recipes_number"))){
                VMchoose = 2;
            }else {
                VMchoose = 1;
            }

            switch (VMchoose){
                case 0:
                    VMMap.put("你喜欢的食物",null);
                    VMMap.put("猜你喜欢",null);
                    break;
                //素菜
                case 1:
                    VMMap.put("你喜欢的食物",VegetableMap);
                    int recipes_ID1 = Integer.parseInt(VegetableMap.get("recipes_id"));
                    Recipes Like_recipe1 = recipesService.SelectByPrimaryKey(recipes_ID1);
                    String[] Like_type1 = Like_recipe1.getRecipesType().split(",");
                    int Like_num1 = 0;
                    while (Like_num1 < Like_type1.length){
                        List<String> Like_recipes = recipesService.SelectByType(Like_type1[Like_num1]);
                        LikeList1.removeAll(Like_recipes);
                        LikeList1.addAll(Like_recipes);
                        Like_num1++;
                    }
                    int randomnumber1 = (int)LikeList1.size()/5;
                    if (randomnumber1 == 0)
                        randomnumber1 = 1;
                    if (randomnumber1 >= 3)
                        randomnumber1 = 3;
                    if (LikeList1.size() > 0){
                        int[] randomrecipes1 = randomCommon(0,LikeList1.size()-1,randomnumber1);
                        for (int randomi1 = 0;randomi1<randomnumber1;randomi1++){
                            LikeList2.add(LikeList1.get(randomrecipes1[randomi1]));
                        }
                    }
                    VMMap.put("猜你喜欢",LikeList2);
                    break;
                //荤菜
                case 2:
                    VMMap.put("你喜欢的食物",MeatMap);
                    int recipes_ID2 = Integer.parseInt(MeatMap.get("recipes_id"));
                    Recipes Like_recipe2 = recipesService.SelectByPrimaryKey(recipes_ID2);
                    String[] Like_type2 = Like_recipe2.getRecipesType().split(",");
                    int Like_num2 = 0;
                    while (Like_num2 < Like_type2.length){
                        List<String> Like_recipes = recipesService.SelectByType(Like_type2[Like_num2]);
                        LikeList1.removeAll(Like_recipes);
                        LikeList1.addAll(Like_recipes);
                        Like_num2++;
                    }
                    int randomnumber2 = (int)LikeList1.size()/5;
                    if (randomnumber2 == 0)
                        randomnumber2 = 1;
                    if (randomnumber2 >= 3)
                        randomnumber2 = 3;

                    if (LikeList1.size() > 0){
                        int[] randomrecipes2 = randomCommon(0,LikeList1.size()-1,randomnumber2);
                        for (int randomi2 = 0;randomi2<randomnumber2;randomi2++){
                            LikeList2.add(LikeList1.get(randomrecipes2[randomi2]));
                        }
                    }
                    VMMap.put("猜你喜欢",LikeList2);
                    break;
            }

            //营养素百分比
            NumberFormat nf = NumberFormat.getNumberInstance();
            // 保留两位小数
            nf.setMaximumFractionDigits(2);
            //能量
            double tage_Energy = 0;
            if (All_Energy == 0){
                tage_Energy = -100;
            }else if (All_Energy/All_Energy2 >2){
                tage_Energy = 100;
            }else {
                String tage_Energystr = nf.format(All_Energy/All_Energy2*100-100);
                tage_Energy = Double.valueOf(tage_Energystr);
            }
            //蛋白质
            double tage_portein = 0;
            if (All_portein == 0){
                tage_portein = -100;
            }else if (All_portein/All_portein2 >2){
                tage_portein = 100;
            }else{
                String tage_porteinstr = nf.format(All_portein/All_portein2*100-100);
                tage_portein = Double.valueOf(tage_porteinstr);
            }
            //脂肪
            double tage_fat = 0;
            if (All_fat == 0){
                tage_fat = -100;
            }else if (All_fat/All_fat2 > 2){
                tage_fat = 100;
            }else {
                String tage_fatstr = nf.format(All_fat/All_fat2*100-100);
                tage_fat = Double.valueOf(tage_fatstr);
            }
            //维生素A
            double tage_vitamin_A = 0;
            if (All_vitamin_A == 0){
                tage_vitamin_A = -100;
            }else if (All_vitamin_A/All_vitamin_A2 >2){
                tage_vitamin_A = 100;
            }else{
                String tage_vitamin_Astr = nf.format(All_vitamin_A/All_vitamin_A2*100-100);
                tage_vitamin_A = Double.valueOf(tage_vitamin_Astr);
            }
            //维生素B1
            double tage_vitamin_B_1 = 0;
            if (All_vitamin_B_1 == 0){
                tage_vitamin_B_1 = -100;
            }else if (All_vitamin_B_1/All_vitamin_B_12 >2){
                tage_vitamin_B_1 = 100;
            }else{
                String tage_vitamin_B_1str = nf.format(All_vitamin_B_1/All_vitamin_B_12*100-100);
                tage_vitamin_B_1 = Double.valueOf(tage_vitamin_B_1str);
            }
            //维生素B2
            double tage_vitamin_B_2 = 0;
            if (All_vitamin_B_2 == 0){
                tage_vitamin_B_2 = -100;
            }else if (All_vitamin_B_2/All_vitamin_B_22 >2){
                tage_vitamin_B_2 = 100;
            }else{
                String tage_vitamin_B_2str = nf.format(All_vitamin_B_2/All_vitamin_B_22*100-100);
                tage_vitamin_B_2 = Double.valueOf(tage_vitamin_B_2str);
            }
            //维生素C
            double tage_vitamin_C = 0;
            if (All_vitamin_C == 0){
                tage_vitamin_C = -100;
            }else if (All_vitamin_C/All_vitamin_C2 > 2){
                tage_vitamin_C = 100;
            } else{
                String tage_vitamin_Cstr = nf.format(All_vitamin_C/All_vitamin_C2*100-100);
                tage_vitamin_C = Double.valueOf(tage_vitamin_Cstr);
//                tage_vitamin_C = (double) df.parse(String.valueOf(All_vitamin_C/All_vitamin_C2*100).substring(5)) - 100;
            }


            List<Double> tage_List = new ArrayList<>();
            tage_List.add(tage_Energy);
            tage_List.add(tage_portein);
            tage_List.add(tage_fat);
            tage_List.add(tage_vitamin_A);
            tage_List.add(tage_vitamin_B_1);
            tage_List.add(tage_vitamin_B_2);
            tage_List.add(tage_vitamin_C);

            //各营养素套餐
            String[] EnergyArray = {"番茄牛肉面","水煮肉片","炒面","糖醋排骨","红烧鲤鱼",
                    "炒面面包","粉蒸肉","阳春面","酸菜鱼","葱油拌面"};
            String[] porteinArray = {"可乐鸡翅","番茄牛肉面","红烧牛肉面","菠菜猪肝汤","红烧鸡块",
                    "炒面","黑椒牛排","乌鸡汤","金针菇鲫鱼汤","豆浆"};
            String[] fatArray = {"水煮肉片","东坡肉","糖醋排骨","红烧排骨","可乐鸡翅",
                    "回锅肉","红烧鲤鱼","粉蒸肉","葱油拌面","干煸豆角"};
            String[] vitamin_AArray = {"菠菜猪肝汤","香菇油菜","番茄牛肉面","白菜炒肉片","可乐鸡翅",
                    "番茄蛋汤","剁椒鱼头","鸡蛋","葱油拌面","东坡肉"};
            String[] vitamin_B_1Array = {"番茄牛肉面","小米粥","红烧牛肉面","炒面","京酱肉丝",
                    "菠菜猪肝汤","阳春面","糖醋排骨","炒面面包","红烧排骨"};
            String[] vitamin_B_2Array = {"菠菜猪肝汤","番茄牛肉面","玫瑰花卷馒头","可乐鸡翅","剁椒鱼头",
                    "酸菜鱼","炒面","金针菇鲫鱼汤","糖醋排骨","乌鸡汤"};
            String[] vitamin_CArray = {"菠菜猪肝汤","番茄牛肉面","酸辣土豆丝","剁椒鱼头","炒青菜",
                    "辣椒炒豆腐干","清水芦笋","白菜炒肉片","土豆饼","红烧牛肉面"};

            //正数
            Map<String, Double> Probs1 = new TreeMap<String, Double>();
            //负数
            Map<String, Double> Probs2 = new TreeMap<String, Double>();
            if (tage_Energy >= 0){
                Probs1.put("能量",tage_Energy);
            }else{
                Probs2.put("能量",tage_Energy);
            }
            if (tage_portein >= 0){
                Probs1.put("蛋白质",tage_portein);
            }else{
                Probs2.put("蛋白质",tage_portein);
            }
            if (tage_fat >= 0){
                Probs1.put("脂肪",tage_fat);
            }else{
                Probs2.put("脂肪",tage_fat);
            }
            if (tage_vitamin_A >= 0){
                Probs1.put("维生素A",tage_vitamin_A);
            }else{
                Probs2.put("维生素A",tage_vitamin_A);
            }
            if (tage_vitamin_B_1 >= 0){
                Probs1.put("维生素B1",tage_vitamin_B_1);
            }else{
                Probs2.put("维生素B1",tage_vitamin_B_1);
            }
            if (tage_vitamin_B_2 >= 0){
                Probs1.put("维生素B2",tage_vitamin_B_2);
            }else{
                Probs2.put("维生素B2",tage_vitamin_B_2);
            }
            if (tage_vitamin_C >= 0){
                Probs1.put("维生素C",tage_vitamin_C);
            }else{
                Probs2.put("维生素C",tage_vitamin_C);
            }

            Map Probs_map = new HashMap();
            int probsnum = 0;
            Probs1 = sortByValueDescending(Probs1);
            Map Probs_map2 = new HashMap();
//            System.out.println("基于value值的降序，排序输出结果为：");
            for (Map.Entry<String, Double> entry : Probs1.entrySet()) {
                if (probsnum == 1)
                    break;
                if (entry.getValue() >= 100){
                    Probs_map2.put("食用过多",entry.getKey());
                }

//                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                probsnum++;
            }
            probsnum = 0;
//            System.out.println("基于value值的升序，排序输出结果为：");
            Probs2 = sortByValueAscending(Probs2);
            if (Probs2.size() == 0){
                Probs_map.put("报告值",0);
            }else{
                for (Map.Entry<String, Double> entry : Probs2.entrySet()) {
                    if (probsnum == 1)
                        break;
                    Probs_map.put("报告值",1);
                    Probs_map.put("食用过少",entry.getKey());
                    int[] arraynum = randomCommon(0,9,3);
                    if (entry.getKey().equals("能量")){
                        Probs_map.put("菜品1",EnergyArray[arraynum[0]]);
                        Probs_map.put("菜品2",EnergyArray[arraynum[1]]);
                        Probs_map.put("菜品3",EnergyArray[arraynum[2]]);
                    }else if (entry.getKey().equals("蛋白质")){
                        Probs_map.put("菜品1",porteinArray[arraynum[0]]);
                        Probs_map.put("菜品2",porteinArray[arraynum[1]]);
                        Probs_map.put("菜品3",porteinArray[arraynum[2]]);
                    }else if (entry.getKey().equals("脂肪")){
                        Probs_map.put("菜品1",fatArray[arraynum[0]]);
                        Probs_map.put("菜品2",fatArray[arraynum[1]]);
                        Probs_map.put("菜品3",fatArray[arraynum[2]]);
                    }else if (entry.getKey().equals("维生素A")){
                        Probs_map.put("菜品1",vitamin_AArray[arraynum[0]]);
                        Probs_map.put("菜品2",vitamin_AArray[arraynum[1]]);
                        Probs_map.put("菜品3",vitamin_AArray[arraynum[2]]);
                    }else if (entry.getKey().equals("维生素B1")){
                        Probs_map.put("菜品1",vitamin_B_1Array[arraynum[0]]);
                        Probs_map.put("菜品2",vitamin_B_1Array[arraynum[1]]);
                        Probs_map.put("菜品3",vitamin_B_1Array[arraynum[2]]);
                    }else if (entry.getKey().equals("维生素B2")){
                        Probs_map.put("菜品1",vitamin_B_2Array[arraynum[0]]);
                        Probs_map.put("菜品2",vitamin_B_2Array[arraynum[1]]);
                        Probs_map.put("菜品3",vitamin_B_2Array[arraynum[2]]);
                    }else if (entry.getKey().equals("维生素C")){
                        Probs_map.put("菜品1",vitamin_CArray[arraynum[0]]);
                        Probs_map.put("菜品2",vitamin_CArray[arraynum[1]]);
                        Probs_map.put("菜品3",vitamin_CArray[arraynum[2]]);
                    }
//                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                    probsnum++;
                }

            }

            FinalMap.put("错误码",200);
            FinalMap.put("光顾次数",DRlist.size());
            FinalMap.put("营养百分比差",tage_List);
            FinalMap.put("健康分析1",Probs_map);
            FinalMap.put("健康分析2",Probs_map2);
            FinalMap.put("购买记录_素菜",VegetableList);
            FinalMap.put("购买记录_荤菜",MeatList);
            FinalMap.put("猜你喜欢分析",VMMap);

            return FinalMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n 随机数个数
     */
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    //降序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    //升序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueAscending(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return compare;
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static List<Map.Entry<Integer,Integer>> getEntryMap(List<Map> Combinelist){
        Map Combmap = CanteenControler.mapCombine(Combinelist);
        Map<Integer, Integer> finalmap = new TreeMap<Integer, Integer>();
        Set<Integer> set = Combmap.keySet(); //取出所有的key值
        for (Integer key:set) {
            List newList = new ArrayList<>();
            newList = (List) Combmap.get(key);
            int lenght = newList.size();
            finalmap.put(key,lenght);
        }
        //这里将map.entrySet()转换成list
        List<Map.Entry<Integer,Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(finalmap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //降序排序
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }

    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public static Date geLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    public static Date geLLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -14);
        return cal.getTime();
    }

    @Test
    public void aaaa() throws JSONException {
        Calendar calendar = Calendar.getInstance();
/* HOUR_OF_DAY 指示一天中的小时 */
calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//System.out.println("一个小时前的时间：" + );
        String dd = df.format(calendar.getTime());
System.out.println("当前的时间：" + df.format(new Date()));

    }

    @RequestMapping("aa")
    public void aa(@RequestParam("heigt") String height,@RequestParam("openid") String openid){
//        if (height.equals(""))
    }

}
