package me.parade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.parade.domain.entity.SysDept;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DeptService {

    /**
     * 获取部门名称
     *
     * @param deptId 部门ID
     * @return 部门名称
     */
    String getDeptNameById(Long deptId);

    /**
     * 获取部门详情
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDept getDeptById(Long deptId);

    /**
     * 获取部门列表
     *
     * @param dept 查询条件
     * @return 部门列表
     */
    List<SysDept> getDeptList(SysDept dept);

    /**
     * 分页查询部门列表
     *
     * @param page 分页参数
     * @param dept 查询条件
     * @return 部门分页列表
     */
    IPage<SysDept> getDeptPage(Page<SysDept> page, SysDept dept);

    /**
     * 获取部门树形结构
     *
     * @param dept 查询条件
     * @return 部门树形结构
     */
    List<SysDept> getDeptTree(SysDept dept);

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    boolean addDept(SysDept dept);

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    boolean updateDept(SysDept dept);

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 是否成功
     */
    boolean deleteDept(Long deptId);

    /**
     * 检查部门是否有子部门
     *
     * @param deptId 部门ID
     * @return 是否有子部门
     */
    boolean hasChildDept(Long deptId);

    /**
     * 检查部门是否有用户
     *
     * @param deptId 部门ID
     * @return 是否有用户
     */
    boolean hasUsers(Long deptId);
    
    /**
     * 获取部门及其所有子部门的ID列表
     *
     * @param deptId 部门ID
     * @return 部门及其子部门ID列表
     */
    List<Long> getDeptAndChildrenIds(Long deptId);
}