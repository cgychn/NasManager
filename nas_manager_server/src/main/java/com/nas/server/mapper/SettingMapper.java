package com.nas.server.mapper;

import com.nas.server.entity.db.Setting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SettingMapper {

    @Select("select * from setting where setting_name = #{settingName}")
    Setting querySetting(@Param("settingName") String settingName);

    @Update("replace into setting(setting_name, value) values(#{settingName}, #{value})")
    void setValue(@Param("settingName") String settingName, @Param("value") String value);
}
