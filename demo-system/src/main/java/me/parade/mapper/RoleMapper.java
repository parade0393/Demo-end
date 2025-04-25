package me.parade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.domain.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询所有可用角色
     *
     * @return 角色列表
     */
    @Select("SELECT * FROM sys_role WHERE status = 1 AND is_deleted = 0 ORDER BY sort")
    List<SysRole> selectAllEnabledRoles();

    /**
     * 根据角色编码查询角色信息
     *
     * @param code 角色编码
     * @return 角色信息
     */
    @Select("SELECT * FROM sys_role WHERE code = #{code} AND is_deleted = 0")
    SysRole selectByCode(@Param("code") String code);
}