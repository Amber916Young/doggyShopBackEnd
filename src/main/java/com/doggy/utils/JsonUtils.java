package com.doggy.utils;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    static {
    	objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }
    /**
     * List<T> 转 json字符串
     */

    public static <T> String listToJson(List<T> ts) {
        return JSON.toJSONString(ts);
    }

    public static HashMap<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {

        }

        return map;
    }

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
    	try {
			String string = objectMapper.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 将json结果集转化为对象
     * 
     * @param jsonData json数据
     * @param clazz 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = objectMapper.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static JsonNode stringToJsonNode(String data) {
        try {
            JsonNode jsonNode = MAPPER.readTree(data);
            return jsonNode;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
    	JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, beanType);
    	try {
    		List<T> list = objectMapper.readValue(jsonData, javaType);
    		return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }


    public static <T> T mapper(JsonNode node , Class<T> valueType) throws Exception{
        if (node == null||node.size()==0)
            return null;
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(node), valueType);
        } catch (Exception e) {
            throw e;
        }
    }

}
