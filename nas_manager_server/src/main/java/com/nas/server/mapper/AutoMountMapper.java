package com.nas.server.mapper;

import com.nas.server.entity.db.AutoMount;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AutoMountMapper {

    @Select("select * from auto_mount")
    List<AutoMount> getAutoMountList();

    @Select("select * from auto_mount where partition_uuid = #{uuid} limit 1")
    AutoMount getAutoMountByPartitionUuid(@Param("uuid") String uuid);

    @Select("select * from auto_mount where mount_point = #{mountPoint} limit 1")
    AutoMount getAutoMountByMountPoint(@Param("mountPoint") String mountPoint);

    @Insert("insert ignore into auto_mount(partition_uuid, mount_point, fstype, other_options) values(#{uuid}, #{mountPoint}, #{fstype}, #{options})")
    void insertAutoMount(@Param("uuid") String uuid, @Param("mountPoint") String mountPoint, @Param("fstype") String fstype, @Param("options") String options);

    @Delete("delete from auto_mount where id = #{id}")
    void deleteAutoMountById(@Param("id") int id);

    @Delete("delete from auto_mount where mount_point = #{mountPoint}")
    void deleteAutoMountByMountPoint(@Param("mountPoint") String mountPoint);

    @Delete("delete from auto_mount where partition_uuid = #{uuid}")
    void deleteAutoMountByUuid(@Param("uuid") String uuid);
}
