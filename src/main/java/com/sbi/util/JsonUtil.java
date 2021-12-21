package com.sbi.util;


import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/***
 * 用jackson包实现json、对象、Map之间的转换
 * @author Pelin
 *
 */
public class JsonUtil {

    /**
     * 将 Array,list,set 解析成 Json 串
     * @return Json 串
     */
    public static String arrayToJsonStr(Object objs){
        JSONArray json = JSONArray.fromObject(objs);
        return json.toString();
    }

    /***
     * 将javabean对象和map对象 解析成 Json 串
     * @param obj
     * @return
     */
    public static String objectToJsonStr(Object obj){
        JSONObject json = JSONObject.fromObject(obj);
        return json.toString();

    }

    /***
     * 将javabean对象或者map对象 解析成 Json 串,使用JsonConfig 过滤属性
     * @param obj
     * @param config
     * @return
     */
    public static String objectToJsonStr(Object obj, JsonConfig config ){
//		JsonConfig config = new JsonConfig();
//	    config.setExcludes(new String[] { "name" });
        JSONObject json = JSONObject.fromObject(obj,config);
        return json.toString();

    }

    /**
     * 将  Json 串 解析成 Array,list,set
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> jsonStrToArray(String jsonStr){
        JSONArray jsonArray = JSONArray.fromObject(jsonStr);
        Object array = JSONArray.toArray(jsonArray);
        return (Collection<T>) array;
    }

    /**
     * 将  Json 串 解析成 Array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] jsonStrToArray(String jsonStr, Class<T> clazz){
        JSONArray jsonArray = JSONArray.fromObject(jsonStr);
        return (T[]) JSONArray.toArray(jsonArray,clazz);
    }
    /**
     * 将  Json 串 解析成 Collection
     * @return
     */
    public static <T> Collection<T> jsonStrToCollection(String jsonStr, Class<T> clazz){
        JSONArray jsonArray = JSONArray.fromObject(jsonStr);
        @SuppressWarnings("unchecked")
        Collection<T>  array = JSONArray.toCollection(jsonArray,clazz);
        return array;
    }

    /**
     * 将  Json 串 解析成 list
     * @return
     */
    public static <T> List<T> jsonStrToList(String jsonStr, Class<T> clazz){
        return (List<T>) jsonStrToCollection(jsonStr,clazz);
    }

    /**
     * 将  Json 串 解析成 Map或者javabean
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonStrToObject(String jsonStr, Class<T> clazz){
        JSONObject json = JSONObject.fromObject(jsonStr);
        Object obj = JSONObject.toBean(json, clazz);
        return (T) obj;
    }

}