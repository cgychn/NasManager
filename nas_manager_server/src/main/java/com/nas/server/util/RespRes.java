package com.nas.server.util;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RespRes {

    public static Map<String, Object> success(Object data, String message) {
        return jsonMap(true, data, message);
    }

    public static Map<String, Object> error(Object data, String message) {
        return jsonMap(false, data, message);
    }

    private static Map<String, Object> jsonMap(boolean res, Object data, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", res ? "success" : "fail");
        map.put("data", JSONObject.toJSON(data));
        map.put("message", message);
        return map;
    }

}
