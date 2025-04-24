package me.parade.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 菜单数据访问接口
 */
@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询所有菜单
     */
    @Select("SELECT * FROM sys_menu WHERE del_flag = 0 ORDER BY parent_id, order_num")
    List<SysMenu> selectAllMenus();

    /**
     * 根据用户ID查询菜单
     */
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "LEFT JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.del_flag = 0 " +
            "ORDER BY m.parent_id, m.order_num")
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 查询所有权限标识
     */
    @Select("SELECT DISTINCT perms FROM sys_menu WHERE perms IS NOT NULL AND perms != '' AND del_flag = 0")
    Set<String> selectAllPerms();

    /**
     * 根据用户ID查询权限标识
     */
    @Select("SELECT DISTINCT m.perms FROM sys_menu m " +
            "LEFT JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.perms IS NOT NULL AND m.perms != '' AND m.del_flag = 0")
    Set<String> selectPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据父ID查询子菜单
     */
    @Select("SELECT * FROM sys_menu WHERE parent_id = #{parentId} AND del_flag = 0 ORDER BY order_num")
    List<SysMenu> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询正常状态的菜单
     */
    @Select("SELECT * FROM sys_menu WHERE status = 0 AND del_flag = 0 ORDER BY parent_id, order_num")
    List<SysMenu> selectNormalMenus();
}