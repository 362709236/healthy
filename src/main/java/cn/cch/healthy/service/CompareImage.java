package cn.cch.healthy.service;

import cn.cch.healthy.util.FaceUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class CompareImage {

    public List<String> test2 (String file) throws IOException {
        String str = FaceUtil.checkFace(file);
        //facetoken 集合
        List<String> faceList = new ArrayList<String>();
        try{
            JSONObject myjsom = new JSONObject(str);
            JSONArray faceArray = myjsom.getJSONArray("faces");

            for (int i = 0; i < faceArray.length(); i++) {
                JSONObject faceObject = faceArray.getJSONObject(i);
                String face_token = faceObject.getString("face_token");
                faceList.add(face_token);
                System.out.println("token"+" "+i+" "+face_token);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return faceList;
    }
}
