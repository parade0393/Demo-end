package me.parade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.entity.UploadLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 上传日志 Mapper 接口
 */
@Mapper
public interface UploadLogMapper extends BaseMapper<UploadLog> {
}

