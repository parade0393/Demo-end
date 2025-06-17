package me.parade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文件信息 Mapper 接口
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 根据文件哈希值查找文件
     *
     * @param fileHash 文件哈希值
     * @return 文件信息
     */
    FileInfo selectByFileHash(@Param("fileHash") String fileHash);

    /**
     * 增加文件引用计数
     *
     * @param fileId 文件ID
     * @return 影响行数
     */
    int incrementReferenceCount(@Param("fileId") Long fileId);

    /**
     * 减少文件引用计数
     *
     * @param fileId 文件ID
     * @return 影响行数
     */
    int decrementReferenceCount(@Param("fileId") Long fileId);
}

