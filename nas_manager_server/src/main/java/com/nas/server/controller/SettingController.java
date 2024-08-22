package com.nas.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.nas.server.service.SettingService;
import com.nas.server.util.RespRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("setting")
public class SettingController {

    private final Logger logger = LoggerFactory.getLogger(SettingController.class);

    @Autowired
    private SettingService settingService;

    @PostMapping("set")
    public Map<String, ?> setValue(@RequestBody JSONObject jsonObject) {
        try {
            String settingName = jsonObject.getString("settingName");
            String value = jsonObject.getString("value");
            settingService.setValue(settingName, value);
            return RespRes.success(null, "");
        } catch (Exception e) {
            logger.error("更新设置失败", e);
            return RespRes.error(null, "");
        }
    }

    public Map<String, ?> getValue(@RequestBody JSONObject jsonObject) {
        try {
            String settingName = jsonObject.getString("settingName");
            return RespRes.success(settingName, "");
        } catch (Exception e) {
            logger.error("查询设置失败", e);
            return RespRes.error(null, "");
        }
    }

}
