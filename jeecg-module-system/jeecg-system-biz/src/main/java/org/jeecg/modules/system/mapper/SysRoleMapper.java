package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.system.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 查询全部的角色（不做租户隔离）
     * @param page
     * @param role
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SysRole> listAllSysRole(@Param("page") Page<SysRole> page, @Param("role") SysRole role);

    /**
     * 查询角色是否存在不做租户隔离
     *
     * @param roleCode
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    SysRole getRoleNoTenant(@Param("roleCode") String roleCode);

    /**
     * 删除角色与用户关系
     * @Author scott
     * @Date 2019/12/13 16:12
     * @param roleId
     */
    @Delete("delete from sys_user_role where role_id = #{roleId}")
    void deleteRoleUserRelation(@Param("roleId") String roleId);


    /**
     * 删除角色与权限关系
     * @Author scott
     * @param roleId
     * @Date 2019/12/13 16:12
     */
    @Delete("delete from sys_role_permission where role_id = #{roleId}")
    void deleteRolePermissionRelation(@Param("roleId") String roleId);

    /**
     * 根据角色id和当前租户判断当前角色是否存在这个租户中
     * @param id
     * @return
     */
    @Select("select count(*) from sys_role where id=#{id} and tenant_id=#{tenantId}")
    Long getRoleCountByTenantId(@Param("id") String id, @Param("tenantId") Integer tenantId);


    @Select("select user_id from sys_user_role where role_id = #{roleId} ")
    List<String> getListIdsByRoleIds(@Param("roleId") String roleId);

    @Select({
            "<script>",
            "SELECT DISTINCT",
            "t2.*",
                    "FROM",
           " sys_user_role t1",
            "LEFT JOIN sys_role t2 ON t1.role_id = t2.id  ",
            "where t1.user_id in",
            "<foreach collection='userIds' item='userIds' open='(' separator=',' close=')'>",
            "#{userIds}",
            "</foreach>",
            "group by t2.id ",
            "</script>"
    })
    List<SysRole> getRoleNameByUserIds(@Param("userIds")List<String> userIds);

    @Select("select t2.*,t1.user_id as userId from sys_user_role  t1 left join sys_role t2 on t1.role_id = t2.id  where t1.user_id = #{userId} ")
    List<SysRole> getRoleNameByUserId(@Param("userId")String userId);

    @Select("select t1.user_id as userId from sys_user_role  t1 left join sys_role t2 on t1.role_id = t2.id  where  t2.role_name = '管理员'")
    List<String> getAllAdminUIds();
}
