package me.parade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_config")
public class SystemConfig {

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置描述
     */
    @TableField("config_desc")
    private String configDesc;

    /**
     * 配置类型(STRING, NUMBER, BOOLEAN, JSON)
     */
    @TableField("config_type")
    private String configType;

    /**
     * 是否为系统配置(0:否, 1:是)
     */
    @TableField("is_system")
    private Boolean isSystem;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 配置类型枚举
     */
    public enum ConfigType {
        STRING("STRING", "字符串"),
        NUMBER("NUMBER", "数字"),
        BOOLEAN("BOOLEAN", "布尔值"),
        JSON("JSON", "JSON对象");

        private final String code;
        private final String desc;

        ConfigType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
