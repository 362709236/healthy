package cn.cch.healthy.controller;

import cn.cch.healthy.model.Illness;
import cn.cch.healthy.model.Occupation;
import cn.cch.healthy.model.UserIllness;
import cn.cch.healthy.model.Userinfo;
import cn.cch.healthy.service.*;
import cn.cch.healthy.util.FaceUtil;
import cn.cch.healthy.util.RecommendUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    @RequestMapping("uploadPic")
    public String SetUserFace(@RequestParam("openid") String openid,MultipartFile file) throws IOException {
        System.out.println(openid);
        if (file == null || "".equals(file.getOriginalFilename())) {
            return "上传的照片为空";
        } else {
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
                //保存用户的facetoken
                Userinfo user = userinfoService.SelectByOpenid(openid);
                user.setUserFaceToken(token);
                int end = userinfoService.UpdateByPrimaryKeySelective(user);
                if (end == 1){
                    return "成功";
                }else{
                    return "失败";
                }
//                User user = new User();
//                user.setFaceToken(token);
//              testservice.add(user);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "系统繁忙，请稍后重试！";
            }
        }
    }

    //比对用户
    @RequestMapping("/compare")
    public Map CompareFace() throws Exception {
        Map map = new HashMap();
        List<String> faceList=testservice.test2();
        List<Userinfo> userList=userinfoService.SelectAll();  //获取所有用户
//        String token="5fa320fbf715f1b0857b555e62d52006";   //测试用
        if (faceList.size()==0||userList.size()==0)
            return map;
        for(int i=0;i<faceList.size();i++){
            for(int j=0;j<userList.size();j++){                 //将拍摄图片所得facetoken与用户的facetoken进行比对
                if (userList.get(j).getUserFaceToken()==null){
                    continue;
                }
                boolean isExit=FaceUtil.compare(userList.get(j).getUserFaceToken(),faceList.get(i));
                if(isExit){
                    //推送内容
                    System.out.println("chenggong");    //测试用
                    RecommendUtil recommendUtil = new RecommendUtil();
                    map=recommendUtil.recommend(1);
                    return map;
                }
            }
        }
        return map;
    }

    @RequestMapping("GetUserData")
    public HashMap GetUserData(@RequestParam("openid") String openid,@RequestParam("username")String name){
        System.out.println("进入获取信息控制器");
        Userinfo user= userinfoService.SelectByOpenid(openid);
        if (user == null){
            Userinfo new_user = new Userinfo();
            new_user.setUserOpenid(openid);
            new_user.setUserName(name);
            userinfoService.insert(new_user);
            user = userinfoService.SelectByOpenid(openid);
        }
        int user_id = user.getUserId();
        List<UserIllness> UIlist = userIllnessService.SelectByUserid(user_id);
        List<String> IllnameList = new ArrayList<String>();
        if (UIlist.size() != 0){
            for (int i = 0;i<UIlist.size();i++){
                int ill_id = UIlist.get(i).getIllId();
                Illness ill = illnessService.SelectByPrimaryKey(ill_id);
                String ill_name = ill.getIllName();
                IllnameList.add(ill_name);
            }
        }

        HashMap<String, Object> map=new HashMap<String, Object>();
        map.put("userName",user.getUserName());
        map.put("userSex",user.getUserSex());
        map.put("userBirthday",user.getUserBirthday());
        map.put("userHeight",user.getUserHeight());
        map.put("userWeight",user.getUserWeight());
        map.put("userCcupation",user.getUserCcupation());
        map.put("userIllness",IllnameList);

        return map;
    }

    @RequestMapping("updateUserInfo")
    public String UpdateUserinfo(@RequestParam("openid") String openid,
                                 @RequestParam("usersex") String sex,@RequestParam("userborn") String birthday,
                                 @RequestParam("userheight") String height_str,@RequestParam("userweight") String weight_str,
                                 @RequestParam("occupation") String occupation,@RequestParam("userills")String Illness_Str) throws ParseException {
        Userinfo user = userinfoService.SelectByOpenid(openid);
        System.out.println("进入修改信息控制器");

        if(!(birthday == null || birthday.equals(""))){
            birthday = birthday.replace("/", "-");
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

        user.setUserSex(sex);

        if (!(height_str == null || height_str.equals(""))){
            double height = Double.valueOf(height_str);
            user.setUserHeight(height);
        }
        if (!(weight_str == null || weight_str.equals(""))){
            double weight = Double.valueOf(weight_str);
            user.setUserWeight(weight);
        }
        user.setUserCcupation(occupation);

        int user_id = user.getUserId();
        List<UserIllness> old_UIlist = userIllnessService.SelectByUserid(user_id);

        ArrayList<Integer> insertList = new ArrayList();
        ArrayList<Integer> deleteList = new ArrayList();
        ArrayList<Integer> restList = new ArrayList();
        HashMap<String, Object> map=new HashMap<String, Object>();

        String[] IllnessArray = Illness_Str.split(",");
        if (!IllnessArray[0].equals("暂无")){
            for (int i = 0;i<IllnessArray.length;i++){
                Illness ill = illnessService.SelectByIllname(IllnessArray[i]);
                int ill_id = ill.getIllId();
                insertList.add(ill_id);
            }
        }else{
            userIllnessService.DeleteByUserid(user_id);
        }

        if (old_UIlist.size() != 0 && IllnessArray.length != 0){
            for (int i = 0;i<old_UIlist.size();i++){
                int old_UIid = old_UIlist.get(i).getIllId();
                deleteList.add(old_UIid);
                for (int j = 0;j<insertList.size();j++){
                    if (insertList.get(j) == old_UIid){
                        restList.add(old_UIid);
                        deleteList.remove(Integer.valueOf(old_UIid));
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
                int ill_id = insertList.get(i);
                UserIllness UI = new UserIllness();
                UI.setIllId(ill_id);
                UI.setUserId(user_id);
                userIllnessService.insert(UI);
            }
        }
        if (deleteList.size() != 0){
            for (int i = 0;i<deleteList.size();i++){
                int ill_id = deleteList.get(i);
                UserIllness UI = new UserIllness();
                UI.setIllId(ill_id);
                UI.setUserId(user_id);
                userIllnessService.DeleteByUserIll(UI);
            }
        }

        int end = userinfoService.UpdateByPrimaryKeySelective(user);

        if (end == 1){
//            map.put("Infomation","成功");
//            map.put("userName",name);
//            map.put("userSex",sex);
//            map.put("userBirthday",birthday);
//            map.put("userHeight",height);
//            map.put("userWeight",weight);
//            map.put("userCcupation",occupation);
            return "成功";
        }
        map.put("Infomation","成功");
        return "失败";
    }

    @RequestMapping("GetIllnessData")
    public List<Illness> GetIllnessData(){
        return illnessService.SelectAll();
    }

    @RequestMapping("GetOccupation")
    public List<Occupation> GetOccupation(){
        return occupationService.SelectAll();
    }

    @RequestMapping("ceshi")
    public String ceshi(@RequestParam("openid") String openid){
        if(openid==null||openid.equals("")){
            System.out.println("ssss");
        }else{
            double dou_openid = Double.valueOf(openid);
            System.out.println(dou_openid);
        }
//        if (){
//            System.out.println("ssss");
//        }

        System.out.println(openid);
        return "成功";
    }

}
