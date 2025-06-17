package me.parade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.entity.FileReference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件引用 Mapper 接口
 */
@Mapper
public interface FileReferenceMapper extends BaseMapper<FileReference> {

    /**
     * 根据文件ID查找所有引用
     *
     * @param fileId 文件ID
     * @return 引用列表
     */
    List<FileReference> selectByFileId(@Param("fileId") Long fileId);

    /**
     * 统计文件的有效引用数量
     *
     * @param fileId 文件ID
     * @return 引用数量
     */
    int countValidReferencesByFileId(@Param("fileId") Long fileId);
}

