package com.pzj.ipcdemo.utils;

import android.content.ContentValues;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONUtil {
    private static final String TAG = JSONUtil.class.getSimpleName();

    private JSONUtil(){}

    private static Gson gson = new Gson();

    /**
     * 传入一个头部，获取头部管控中的所有String信息
     * @return
     */
    public static String getHeadContext(String jsonData, String head) {
        String jsonObjectString = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonObjectString = jsonObject.get(head).toString();
            // LogUtil.d(TAG, "getHeadContext 只去头部header的数据信息：" + jsonObjectString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObjectString;
    }

    /**
     * 将一个对象转换成一个Json字符串
     * @param t
     * @return
     */
    public static <T> String objectToJson(T t){
        if (t instanceof String) {
            return t.toString();
        } else {
            return gson.toJson(t);
        }
    }

    /**
     * 将Json字符串转换成对应对象
     * @param jsonString    Json字符串
     * @param clazz        对应字节码文件.class
     * @return
     */
    @SuppressWarnings("unchecked")
    public static<T> T jsonToObject(String jsonString, Class<T> clazz){
        if (clazz == String.class) {
            return (T) jsonString;
        } else {
            return (T)gson.fromJson(jsonString, clazz);
        }
    }

    /**
     * 将List集合转换为json字符串
     * @param list    List集合
     * @return
     */
    public static<T> String listToJson(List<T> list){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
            for (int i = 0; i < list.size(); i++) {
                jsonObject = new JSONObject(objectToJson(list.get(i)));
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (jsonObject != null) {
                jsonObject = null;
            }
        }
        return jsonArray.toString();
    }

    /**
     * 将数组转换成json字符串
     * @param array        数组
     * @return
     */
    public static<T> String arrayToJson(T[] array){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
            for (int i = 0; i < array.length; i++) {
                jsonObject = new JSONObject(objectToJson(array[i]));
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (jsonObject != null) {
                jsonObject = null;
            }
        }
        return jsonArray.toString();
    }

    /**
     * 获取json字符串中的值
     * @param json    json字符串
     * @param key    键值
     * @param clazz    所取数据类型，例如：Integer.class，String.class，Double.class，JSONObject.class
     * @return  存在则返回正确值，不存在返回null
     */
    public static<T> T getJsonObjectValue(String json, String key, Class<T> clazz){
        try {
            return getJsonObjectValue(new JSONObject(json), key, clazz);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取jsonObject对象中的值
     * @param jsonObject    jsonObject对象
     * @param key    键值
     * @param clazz    所取数据类型，例如：Integer.class，String.class，Double.class，JSONObject.class
     * @return  存在则返回正确值，不存在返回null
     */
    @SuppressWarnings("unchecked")
    public static<T> T getJsonObjectValue(JSONObject jsonObject, String key, Class<T> clazz){
        T t = null;
        try {
            if (clazz == Integer.class) {
                t = (T) Integer.valueOf(jsonObject.getInt(key));
            }else if(clazz == Boolean.class){
                t = (T) Boolean.valueOf(jsonObject.getBoolean(key));
            }else if(clazz == String.class){
                t = (T) String.valueOf(jsonObject.getString(key));
            }else if(clazz == Double.class){
                t = (T) Double.valueOf(jsonObject.getDouble(key));
            }else if(clazz == JSONObject.class){
                t = (T) jsonObject.getJSONObject(key);
            }else if(clazz == JSONArray.class){
                t = (T) jsonObject.getJSONArray(key);
            }else if(clazz == Long.class){
                t = (T) Long.valueOf(jsonObject.getLong(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * json字符串转换为ContentValues
     * @param json    json字符串
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static ContentValues jsonToContentValues(String json){
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator iterator = jsonObject.keys();
            String key;
            Object value;
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                value = jsonObject.get(key);
                String valueString = value.toString();
                if (value instanceof String) {
                    contentValues.put(key, valueString);
                }else if(value instanceof Integer){
                    contentValues.put(key, Integer.valueOf(valueString));
                }else if(value instanceof Long){
                    contentValues.put(key, Long.valueOf(valueString));
                }else if(value instanceof Double){
                    contentValues.put(key, Double.valueOf(valueString));
                }else if(value instanceof Float){
                    contentValues.put(key, Float.valueOf(valueString));
                }else if(value instanceof Boolean){
                    contentValues.put(key, Boolean.valueOf(valueString));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new Error("Json字符串不合法：" + json);
        }

        return contentValues;
    }
    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String gsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T gsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * 转成list
     * 泛型在编译期类型被擦除导致报错
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> gsonToList(String gsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * 转成list
     * 解决泛型问题
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static  <T> List<T> jsonToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        try {

            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for(final JsonElement elem : array){
                list.add(gson.fromJson(elem, cls));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }


    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> gsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> gsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    // -------

    /**
     * 按章节点得到相应的内容
     * @param jsonString json字符串
     * @param note 节点
     * @return 节点对应的内容
     */
    public static String getNoteJsonString(String jsonString,String note){
        if(TextUtils.isEmpty(jsonString)){
            throw new RuntimeException("json字符串");
        }
        if(TextUtils.isEmpty(note)){
            throw new RuntimeException("note标签不能为空");
        }
        JsonElement element = new JsonParser().parse(jsonString);
        if(element.isJsonNull()){
            throw new RuntimeException("得到的jsonElement对象为空");
        }
        return element.getAsJsonObject().get(note).toString();
    }

    /**
     * 按照节点得到节点内容，然后传化为相对应的bean数组
     * @param jsonString 原json字符串
     * @param note 节点标签
     * @param beanClazz 要转化成的bean class
     * @return 返回bean的数组
     */
    public static <T> List<T> parserJsonToArrayBeans(String jsonString,String note,Class<T> beanClazz){
        String noteJsonString = getNoteJsonString(jsonString,note);
        return parserJsonToArrayBeans(noteJsonString,beanClazz);
    }
    /**
     * 按照节点得到节点内容，转化为一个数组
     * @param jsonString json字符串
     * @param beanClazz 集合里存入的数据对象
     * @return 含有目标对象的集合
     */
    public static <T> List<T> parserJsonToArrayBeans(String jsonString,Class<T> beanClazz){
        if(TextUtils.isEmpty(jsonString)){
            throw new RuntimeException("json字符串为空");
        }
        JsonElement jsonElement = new JsonParser().parse(jsonString);
        if(jsonElement.isJsonNull()){
            throw new RuntimeException("得到的jsonElement对象为空");
        }
        if(!jsonElement.isJsonArray()){
            throw new RuntimeException("json字符不是一个数组对象集合");
        }
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<T> beans = new ArrayList<T>();
        for (JsonElement jsonElement2: jsonArray) {
            T bean = new Gson().fromJson(jsonElement2, beanClazz);
            beans.add(bean);
        }
        return beans;
    }

    /**
     * 把相对应节点的内容封装为对象
     * @param jsonString json字符串
     * @param clazzBean  要封装成的目标对象
     * @return 目标对象
     */
    public static <T> T parserJsonToArrayBean(String jsonString,Class<T> clazzBean){
        if(TextUtils.isEmpty(jsonString)){
            throw new RuntimeException("json字符串为空");
        }
        JsonElement jsonElement = new JsonParser().parse(jsonString);
        if(jsonElement.isJsonNull()){
            throw new RuntimeException("json字符串为空");
        }
        if(!jsonElement.isJsonObject()){
            throw new RuntimeException("json不是一个对象");
        }
        return new Gson().fromJson(jsonElement, clazzBean);
    }
    /**
     * 按照节点得到节点内容，转化为一个数组
     * @param jsonString json字符串
     * @param note json标签
     * @param clazzBean 集合里存入的数据对象
     * @return 含有目标对象的集合
     */
    public static <T> T parserJsonToArrayBean(String jsonString,String note,Class<T> clazzBean){
        String noteJsonString = getNoteJsonString(jsonString, note);
        return parserJsonToArrayBean(noteJsonString, clazzBean);
    }

    /**
     * 把bean对象转化为json字符串
     * @param obj bean对象
     * @return 返回的是json字符串
     */
    public static String toJsonString(Object obj){
        if(obj!=null){
            return new Gson().toJson(obj);
        }else{
            throw new RuntimeException("对象不能为空");
        }
    }

}
