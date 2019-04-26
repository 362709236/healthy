package cn.cch.healthy.service;

import cn.cch.healthy.model.Illness;
import cn.cch.healthy.model.UserIllness;
import cn.cch.healthy.model.UserInterest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserConnentService {

    @Autowired
    UserIllnessService userIllnessService;

    @Autowired
    IllnessService illnessService;

    @Autowired
    UserinfoService userinfoService;

    @Autowired
    InterestService interestService;

    public void ConnectIllness(int user_id,String Illness_Str){
        List<Integer> old_UIlist = userIllnessService.SelectByUserid(user_id);

        ArrayList<Integer> insertList = new ArrayList();
        ArrayList<Integer> deleteList = new ArrayList();
        ArrayList<Integer> restList = new ArrayList();

        String[] IllnessArray = Illness_Str.split(",");
        if (!IllnessArray[0].equals("")){
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
                int old_UIid = old_UIlist.get(i);
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
    }

    public void ConnectInterest(int user_id,String interestStr){
        List<Integer> old_UIlist = interestService.SelectByUserId(user_id);

        ArrayList<Integer> insertList = new ArrayList();
        ArrayList<Integer> deleteList = new ArrayList();
        ArrayList<Integer> restList = new ArrayList();

        String[] interestArray = interestStr.split(",");
        if (!(interestArray[0].equals(""))){
            for (int i = 0;i<interestArray.length;i++){
                int interest_id = interestService.GetID(interestArray[i]);
                insertList.add(interest_id);
            }
        }else {
            interestService.DeleteByuserId(user_id);
        }

        if (old_UIlist.size() != 0 && interestArray.length != 0){
            for (int i = 0;i<old_UIlist.size();i++){
                int old_UIid = old_UIlist.get(i);
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
                int interest_id = insertList.get(i);
                UserInterest UI = new UserInterest();
                UI.setInterestId(interest_id);
                UI.setUserId(user_id);
                interestService.insertuserInterest(UI);
            }
        }
        if (deleteList.size() != 0){
            for (int i = 0;i<deleteList.size();i++){
                int interest_id = deleteList.get(i);
                UserInterest UI = new UserInterest();
                UI.setInterestId(interest_id);
                UI.setUserId(user_id);
                interestService.DeleteByUserInterest(UI);
            }
        }
    }
}
