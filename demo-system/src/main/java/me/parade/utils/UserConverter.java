package me.parade.utils;

import me.parade.domain.entity.SysUser;
import me.parade.domain.vo.UserVO;
import me.parade.service.DeptService;
import me.parade.service.RoleService;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户对象转换工具类
 * 用于将用户实体对象转换为视图对象
 */
public class UserConverter {

    /**
     * 将用户实体转换为视图对象
     *
     * @param user 用户实体
     * @param roleService 角色服务
     * @param deptService 部门服务
     * @return 用户视图对象
     */
    public static UserVO convertToUserVO(SysUser user, RoleService roleService, DeptService deptService) {
        if (user == null) {
            return null;
        }

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 设置状态名称
        vo.setStatusName(user.getStatus() != null && user.getStatus() == 1 ? "正常" : "禁用");

        // 设置部门名称
        if (user.getDeptId() != null) {
            vo.setDeptName(deptService.getDeptNameById(user.getDeptId()));
        }

        // 设置角色信息
        List<Long> roleIds = roleService.getUserRoleIds(user.getId());
        vo.setRoleIds(roleIds);

        // 设置角色名称列表
        if (roleIds != null && !roleIds.isEmpty()) {
            List<String> roleNames = new ArrayList<>();
            for (Long roleId : roleIds) {
                String roleName = roleService.getRoleNameById(roleId);
                if (roleName != null) {
                    roleNames.add(roleName);
                }
            }
            vo.setRoleNames(roleNames);
        }

        return vo;
    }
}