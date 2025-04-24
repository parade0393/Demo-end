package me.parade.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.parade.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND del_flag = 0")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回1，不存在返回0
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND del_flag = 0")
    int checkUsernameExists(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @param userId 用户ID（更新时排除自己）
     * @return 存在返回1，不存在返回0
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE email = #{email} AND id != #{userId} AND del_flag = 0")
    int checkEmailExists(@Param("email") String email, @Param("userId") Long userId);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @param userId 用户ID（更新时排除自己）
     * @return 存在返回1，不存在返回0
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE phone = #{phone} AND id != #{userId} AND del_flag = 0")
    int checkPhoneExists(@Param("phone") String phone, @Param("userId") Long userId);
}