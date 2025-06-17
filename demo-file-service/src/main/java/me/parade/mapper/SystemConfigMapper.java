package me.parade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统配置 Mapper 接口
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键查找配置
     *
     * @param configKey 配置键
     * @return 系统配置
     */
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);
}

