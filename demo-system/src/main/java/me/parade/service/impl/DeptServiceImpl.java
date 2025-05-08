package me.parade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.parade.domain.entity.SysDept;
import me.parade.domain.entity.SysUser;
import me.parade.mapper.DeptMapper;
import me.parade.mapper.UserMapper;
import me.parade.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, SysDept> implements DeptService {

    private final DeptMapper deptMapper;
    private final UserMapper userMapper;

    @Override
    public String getDeptNameById(Long deptId) {
        if (deptId == null) {
            return null;
        }
        SysDept dept = deptMapper.selectById(deptId);
        return dept != null ? dept.getName() : null;
    }

    @Override
    public SysDept getDeptById(Long deptId) {
        return deptMapper.selectById(deptId);
    }

    @Override
    public List<SysDept> getDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (dept != null) {
            // 按部门名称模糊查询
            if (StringUtils.hasText(dept.getName())) {
                queryWrapper.like(SysDept::getName, dept.getName());
            }
            
            // 按状态查询
            if (dept.getStatus() != null) {
                queryWrapper.eq(SysDept::getStatus, dept.getStatus());
            }
            
            // 按父部门ID查询
            if (dept.getParentId() != null) {
                queryWrapper.eq(SysDept::getParentId, dept.getParentId());
            }
        }
        
        // 只查询未删除的部门
        queryWrapper.eq(SysDept::getIsDeleted, 0);
        
        // 按排序字段升序排序
        queryWrapper.orderByAsc(SysDept::getSort);
        
        return deptMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<SysDept> getDeptPage(Page<SysDept> page, SysDept dept) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (dept != null) {
            // 按部门名称模糊查询
            if (StringUtils.hasText(dept.getName())) {
                queryWrapper.like(SysDept::getName, dept.getName());
            }
            
            // 按状态查询
            if (dept.getStatus() != null) {
                queryWrapper.eq(SysDept::getStatus, dept.getStatus());
            }
        }
        
        // 只查询未删除的部门
        queryWrapper.eq(SysDept::getIsDeleted, 0);
        
        // 按排序字段升序排序
        queryWrapper.orderByAsc(SysDept::getSort);
        
        return deptMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<SysDept> getDeptTree(SysDept dept) {
        // 查询所有部门列表
        List<SysDept> deptList = getDeptList(dept);
        
        // 构建树形结构
        return buildDeptTree(deptList);
    }

    /**
     * 构建部门树形结构
     *
     * @param deptList 部门列表
     * @return 树形结构列表
     */
    private List<SysDept> buildDeptTree(List<SysDept> deptList) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = deptList.stream().map(SysDept::getId).collect(Collectors.toList());
        
        for (SysDept dept : deptList) {
            // 如果是顶级节点，遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(deptList, dept);
                returnList.add(dept);
            }
        }
        
        if (returnList.isEmpty()) {
            returnList = deptList;
        }
        
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list 部门列表
     * @param t 部门信息
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     *
     * @param list 部门列表
     * @param t 部门信息
     * @return 子部门列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        for (SysDept n : list) {
            if (n.getParentId() != null && n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     *
     * @param list 部门列表
     * @param t 部门信息
     * @return 是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDept(SysDept dept) {
        return deptMapper.insert(dept) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(SysDept dept) {
        return deptMapper.updateById(dept) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDept(Long deptId) {
        // 检查是否有子部门
        if (hasChildDept(deptId)) {
            return false;
        }
        
        // 检查是否有用户
        if (hasUsers(deptId)) {
            return false;
        }
        
        // 逻辑删除部门
        SysDept dept = new SysDept();
        dept.setId(deptId);
        dept.setIsDeleted((byte) 1);
        
        return deptMapper.updateById(dept) > 0;
    }
    
    @Override
    public boolean hasChildDept(Long deptId) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getParentId, deptId);
        queryWrapper.eq(SysDept::getIsDeleted, 0);
        
        return deptMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public boolean hasUsers(Long deptId) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeptId, deptId);
        queryWrapper.eq(SysUser::getIsDeleted, 0);
        
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public List<Long> getDeptAndChildrenIds(Long deptId) {
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(deptId); // 添加当前部门ID
        
        // 递归获取所有子部门
        findChildDeptIds(deptId, deptIds);
        
        return deptIds;
    }
    
    /**
     * 递归查找所有子部门ID
     *
     * @param deptId 部门ID
     * @param deptIds 部门ID列表
     */
    private void findChildDeptIds(Long deptId, List<Long> deptIds) {
        // 查询子部门
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getParentId, deptId);
        queryWrapper.eq(SysDept::getIsDeleted, 0);
        
        List<SysDept> childDepts = this.list(queryWrapper);
        
        // 添加子部门ID并递归查询
        for (SysDept dept : childDepts) {
            deptIds.add(dept.getId());
            findChildDeptIds(dept.getId(), deptIds);
        }
    }
}