package me.parade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.parade.annotation.ResponseResult;
import me.parade.domain.entity.SysDept;
import me.parade.security.annotation.RequiresPermission;
import me.parade.service.DeptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/dept")
@Tag(name = "部门接口", description = "提供部门管理相关接口")
@RequiredArgsConstructor
@ResponseResult
public class DeptController {

    private final DeptService deptService;

    /**
     * 获取部门列表
     *
     * @param dept 查询条件
     * @return 部门列表
     */
    @Operation(summary = "获取部门列表", description = "获取部门列表，可根据部门名称、状态过滤")
    @GetMapping("/list")
    @RequiresPermission("system:dept:query")
    public List<SysDept> getDeptList(SysDept dept) {
        return deptService.getDeptList(dept);
    }

    /**
     * 获取部门树形结构
     *
     * @param dept 查询条件
     * @return 部门树形结构
     */
    @Operation(summary = "获取部门树形结构", description = "获取部门树形结构，可根据部门名称、状态过滤")
    @GetMapping("/tree")
    @RequiresPermission("system:dept:query")
    public List<SysDept> getDeptTree(SysDept dept) {
        return deptService.getDeptTree(dept);
    }

    /**
     * 获取部门详情
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Operation(summary = "获取部门详情", description = "根据部门ID获取部门详情")
    @GetMapping("/{deptId}")
    @RequiresPermission("system:dept:query")
    public SysDept getDeptDetail(@Parameter(description = "部门ID") @PathVariable Long deptId) {
        return deptService.getDeptById(deptId);
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    @Operation(summary = "新增部门", description = "新增部门信息")
    @PostMapping("/create")
    @RequiresPermission("system:dept:add")
    public boolean createDept(@Parameter(description = "部门信息") @RequestBody @Valid SysDept dept) {
        return deptService.addDept(dept);
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    @Operation(summary = "修改部门", description = "修改部门信息")
    @PostMapping("/update")
    @RequiresPermission("system:dept:edit")
    public boolean updateDept(@Parameter(description = "部门信息") @RequestBody @Valid SysDept dept) {
        if (dept.getId() == null) {
            return false;
        }
        return deptService.updateDept(dept);
    }

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 是否成功
     */
    @Operation(summary = "删除部门", description = "根据部门ID删除部门")
    @GetMapping("/delete")
    @RequiresPermission("system:dept:delete")
    public boolean deleteDept(@Parameter(description = "部门ID") @RequestParam Long deptId) {
        // 检查是否有子部门
        if (deptService.hasChildDept(deptId)) {
            return false;
        }
        
        // 检查是否有用户
        if (deptService.hasUsers(deptId)) {
            return false;
        }
        
        return deptService.deleteDept(deptId);
    }
}