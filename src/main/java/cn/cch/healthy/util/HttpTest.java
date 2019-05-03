package cn.cch.healthy.util;

import com.alibaba.fastjson.JSON;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HttpTest {
//    public static final String ADD_URL = "http://localhost:8081/demo2/demo";
//    http://47.101.179.98/wechat/returnpost2
    public static final String ADD_URL = "https://doodoodoo.cn/wechat/send";
    //   public static final String ADD_URL = "http://192.168.4.97:35357/v2.0/tokens";
    public static void appadd(JSONObject json) throws IOException {
        System.out.println("json："+json);
        HttpURLConnection connection=null;
        try {
            //创建连接
            URL url = new URL(ADD_URL);
            connection = (HttpURLConnection) url.openConnection();


            //设置http连接属性

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST"); // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);

            //设置http头 消息
            connection.setRequestProperty("Content-Type","application/json");  //设定 请求格式 json，也可以设定xml格式的
            //connection.setRequestProperty("Content-Type", "text/xml");   //设定 请求格式 xml，
            connection.setRequestProperty("Accept","application/json");//设定响应的信息的格式为 json，也可以设定xml格式的
//             connection.setRequestProperty("X-Auth-Token","xx");  //特定http服务器需要的信息，根据服务器所需要求添加
            connection.connect();

            //添加 请求内容

            //构建嵌套的 json数据

            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("app_name", "asdf");
            obj.put("app_ip", "10.21.243.234");
            obj.put("app_port", 8080);
            obj.put("app_type", "001");
            obj.put("app_area", "asd");

            out.writeUTF(String.valueOf(json));
//            out.writeBytes(String.valueOf(json));
            out.flush();
            out.close();

//            读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println("sb:"+sb);
            reader.close();
////              断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
