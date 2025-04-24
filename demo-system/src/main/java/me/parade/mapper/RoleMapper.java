package me.parade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.domain.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 角色数据访问接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {

    /**
     * 查询用户角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "LEFT JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.del_flag = 0 AND r.status = 0")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户角色标识列表
     */
    @Select("SELECT DISTINCT r.role_key FROM sys_role r " +
            "LEFT JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.del_flag = 0 AND r.status = 0")
    Set<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    /**
     * 查询所有正常状态的角色
     */
    @Select("SELECT * FROM sys_role WHERE status = 0 AND del_flag = 0")
    List<SysRole> selectNormalRoles();

    /**
     * 检查角色名是否存在
     */
    @Select("SELECT COUNT(*) FROM sys_role WHERE role_name = #{roleName} AND id != #{roleId} AND del_flag = 0")
    int checkRoleNameExists(@Param("roleName") String roleName, @Param("roleId") Long roleId);

    /**
     * 检查角色权限字符是否存在
     */
    @Select("SELECT COUNT(*) FROM sys_role WHERE role_key = #{roleKey} AND id != #{roleId} AND del_flag = 0")
    int checkRoleKeyExists(@Param("roleKey") String roleKey, @Param("roleId") Long roleId);

    /**
     * 根据用户ID查询角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询角色是否被分配
     */
    @Select("SELECT COUNT(*) FROM sys_user_role WHERE role_id = #{roleId}")
    int countUserRoleByRoleId(@Param("roleId") Long roleId);
}