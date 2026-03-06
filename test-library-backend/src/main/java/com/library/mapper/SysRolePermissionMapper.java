package com.library.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRolePermissionMapper {

    /**
     * 根据角色ID列表查询权限标识列表
     */
    @Select("<script>" +
            "SELECT p.perm_key FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.perm_id " +
            "WHERE rp.role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<String> selectPermKeysByRoleIds(List<Long> roleIds);
}
