package com.nas.server.service;

import com.nas.server.entity.db.Setting;
import com.nas.server.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

    @Autowired
    private SettingMapper settingMapper;

    public void setValue(String settingName, String value) {
        settingMapper.setValue(settingName, value);
    }

    public Setting getSetting(String settingName) {
        return settingMapper.querySetting(settingName);
    }

}
