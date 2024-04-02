package com.nas.server.mapper;

import com.nas.server.entity.db.NetMount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NetMountMapper {

    @Select("select * from net_mount")
    List<NetMount> getNetMountList();

}
