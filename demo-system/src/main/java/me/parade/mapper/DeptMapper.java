package me.parade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.domain.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface DeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门列表
     *
     * @param dept 部门信息
     * @return 部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    int selectNormalChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 修改子元素关系
     *
     * @param depts 子元素
     * @return 结果
     */
    int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 根据ID查询所有子部门
     *
     * @param deptId 部门ID
     * @return 部门列表
     */
    List<SysDept> selectChildrenDeptById(@Param("deptId") Long deptId);
}