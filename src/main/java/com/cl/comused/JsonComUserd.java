package com.cl.comused;


import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;


/**
 * 常用json---object转换
 * 尝试自己封装反射方法
 */
public class JsonComUserd {
    public static void main(String[] args) throws IOException {

        TestA[] objArr=new TestA[2];
        objArr[0]=new TestA("a1", 1, "c1");
        objArr[1]=new TestA("a2", 2, "c2");

        JSONArray jsonArray = objArrToJsonArr(objArr);
        System.out.println("jsonArray:"+jsonArray);
        JSONObject jsonObject = objToJson(new TestA("a", 1, "c"));
        System.out.println(jsonObject);


        TestA testA = jsonToObj(jsonObject, TestA.class);

        TestA[] testAS = jsonArrToObjArr(jsonArray, TestA.class);
        System.out.println(Arrays.toString(testAS));
    }

    // object ----> json
    public static JSONObject objToJson(TestA obj){
        return JSONObject.fromObject(obj);
    }

    // json ----> object
    public static <T> T jsonToObj(JSONObject jsonObject, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        Object o = objectMapper.readValue(jsonObject.toString(), clazz);
        return (T) o;
    }

    //object数组 ----> jsonArray
    public static JSONArray objArrToJsonArr(TestA[] objArr){
        JSONArray jsonArray = JSONArray.fromObject(objArr);
        return jsonArray;
    }

    // jsonArrar ----> object数组
    public static <T> T[] jsonArrToObjArr(JSONArray jsonArray, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        T[] arrayT = (T[]) Array.newInstance(clazz, jsonArray.size());
        for(int i=0;i<jsonArray.size();i++){
            arrayT[i]=objectMapper.readValue(jsonArray.get(i).toString(), clazz);
        }
        return arrayT;
    }




}


