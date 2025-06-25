# SpringBoot中Redis的应用场景与实现

## 目录
1. [Redis在SpringBoot中的基础配置](#redis在springboot中的基础配置)
2. [缓存功能](#缓存功能)
3. [分布式锁](#分布式锁)
4. [会话管理](#会话管理)
5. [消息队列](#消息队列)
6. [限流功能](#限流功能)
7. [数据统计](#数据统计)
8. [排行榜功能](#排行榜功能)
9. [验证码场景详解](#验证码场景详解)

---

## Redis在SpringBoot中的基础配置

### 1. 依赖配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

### 2. 配置文件

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 10000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
```

### 3. Redis配置类

```java
@Configuration
@EnableCaching
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LazyLoadingEnabled.LAZY, ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
        
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
```

---

## 缓存功能

### 1. 注解式缓存

```java
@Service
public class UserService {
    
    @Cacheable(value = "user", key = "#id")
    public User getUserById(Long id) {
        // 从数据库查询用户
        return userRepository.findById(id).orElse(null);
    }
    
    @CachePut(value = "user", key = "#user.id")
    public User updateUser(User user) {
        // 更新用户信息
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "user", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @CacheEvict(value = "user", allEntries = true)
    public void clearAllUserCache() {
        // 清空所有用户缓存
    }
}
```

### 2. 手动缓存操作

```java
@Component
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void setCache(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    public Object getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }
    
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
```

---

## 分布式锁

### 1. 基于Redis的分布式锁实现

```java
@Component
public class RedisDistributedLock {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String LOCK_PREFIX = "distributed_lock:";
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "return redis.call('del', KEYS[1]) " +
        "else return 0 end";
    
    /**
     * 获取分布式锁
     * @param lockKey 锁的key
     * @param requestId 请求标识
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, int expireTime) {
        String key = LOCK_PREFIX + lockKey;
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, requestId, expireTime, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }
    
    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        String key = LOCK_PREFIX + lockKey;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(UNLOCK_SCRIPT);
        script.setResultType(Long.class);
        
        Long result = redisTemplate.execute(script, Collections.singletonList(key), requestId);
        return Long.valueOf(1).equals(result);
    }
}
```

### 2. 分布式锁使用示例

```java
@Service
public class OrderService {
    
    @Autowired
    private RedisDistributedLock distributedLock;
    
    public void processOrder(String orderId) {
        String lockKey = "order_process_" + orderId;
        String requestId = UUID.randomUUID().toString();
        
        try {
            // 尝试获取锁，等待最多10秒
            if (distributedLock.tryLock(lockKey, requestId, 30)) {
                // 处理订单逻辑
                doProcessOrder(orderId);
            } else {
                throw new BusinessException("订单正在处理中，请稍后再试");
            }
        } finally {
            // 释放锁
            distributedLock.releaseLock(lockKey, requestId);
        }
    }
    
    private void doProcessOrder(String orderId) {
        // 具体的订单处理逻辑
    }
}
```

---

## 会话管理

### 1. Spring Session配置

```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {
    // Spring Session会自动配置Redis作为Session存储
}
```

### 2. 自定义会话管理

```java
@Component
public class SessionManager {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String SESSION_PREFIX = "session:";
    private static final int DEFAULT_TIMEOUT = 1800; // 30分钟
    
    public void createSession(String sessionId, Object userInfo) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.opsForValue().set(key, userInfo, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }
    
    public Object getSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        return redisTemplate.opsForValue().get(key);
    }
    
    public void refreshSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.expire(key, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }
    
    public void removeSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }
}
```

---

## 消息队列

### 1. Redis发布订阅模式

```java
@Configuration
public class RedisMessageConfig {
    
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("order.*"));
        return container;
    }
    
    @Bean
    public MessageListenerAdapter listenerAdapter(OrderMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}

@Component
public class OrderMessageReceiver {
    
    public void receiveMessage(String message) {
        System.out.println("收到订单消息: " + message);
        // 处理订单消息
    }
}

@Service
public class MessagePublisher {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void publishOrderMessage(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
```

### 2. Redis Stream实现

```java
@Service
public class RedisStreamService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void addMessage(String streamKey, Map<String, Object> message) {
        redisTemplate.opsForStream().add(streamKey, message);
    }
    
    public List<MapRecord<String, Object, Object>> readMessages(String streamKey, String consumerGroup) {
        return redisTemplate.opsForStream()
                .read(Consumer.from(consumerGroup, "consumer1"),
                      StreamReadOptions.empty().count(10),
                      StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
    }
}
```

---

## 限流功能

### 1. 基于Redis的令牌桶限流

```java
@Component
public class RedisRateLimiter {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String RATE_LIMIT_SCRIPT = 
        "local key = KEYS[1]\n" +
        "local capacity = tonumber(ARGV[1])\n" +
        "local tokens = tonumber(ARGV[2])\n" +
        "local interval = tonumber(ARGV[3])\n" +
        "local current = redis.call('hmget', key, 'tokens', 'timestamp')\n" +
        "local currentTokens = tonumber(current[1]) or capacity\n" +
        "local lastRefill = tonumber(current[2]) or 0\n" +
        "local now = redis.call('time')[1]\n" +
        "local tokensToAdd = math.floor((now - lastRefill) / interval) * tokens\n" +
        "currentTokens = math.min(capacity, currentTokens + tokensToAdd)\n" +
        "if currentTokens >= 1 then\n" +
        "  currentTokens = currentTokens - 1\n" +
        "  redis.call('hmset', key, 'tokens', currentTokens, 'timestamp', now)\n" +
        "  redis.call('expire', key, capacity * interval)\n" +
        "  return 1\n" +
        "else\n" +
        "  redis.call('hmset', key, 'tokens', currentTokens, 'timestamp', now)\n" +
        "  redis.call('expire', key, capacity * interval)\n" +
        "  return 0\n" +
        "end";
    
    public boolean isAllowed(String key, int capacity, int tokens, int interval) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(RATE_LIMIT_SCRIPT);
        script.setResultType(Long.class);
        
        Long result = redisTemplate.execute(script, 
                Collections.singletonList("rate_limit:" + key),
                String.valueOf(capacity),
                String.valueOf(tokens),
                String.valueOf(interval));
        
        return Long.valueOf(1).equals(result);
    }
}
```

### 2. 限流注解实现

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default "";
    int capacity() default 10;
    int tokens() default 1;
    int interval() default 1;
}

@Aspect
@Component
public class RateLimitAspect {
    
    @Autowired
    private RedisRateLimiter rateLimiter;
    
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        String key = rateLimit.key();
        if (StringUtils.isEmpty(key)) {
            key = point.getSignature().toShortString();
        }
        
        if (rateLimiter.isAllowed(key, rateLimit.capacity(), rateLimit.tokens(), rateLimit.interval())) {
            return point.proceed();
        } else {
            throw new BusinessException("请求过于频繁，请稍后再试");
        }
    }
}
```

---

## 数据统计

### 1. 访问统计

```java
@Service
public class StatisticsService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 记录页面访问量
     */
    public void recordPageView(String page) {
        String key = "page_view:" + page;
        redisTemplate.opsForValue().increment(key);
        
        // 按日期统计
        String dateKey = "page_view:" + page + ":" + LocalDate.now().toString();
        redisTemplate.opsForValue().increment(dateKey);
        redisTemplate.expire(dateKey, 30, TimeUnit.DAYS);
    }
    
    /**
     * 记录用户活跃度
     */
    public void recordUserActivity(Long userId) {
        String today = LocalDate.now().toString();
        String key = "active_users:" + today;
        redisTemplate.opsForSet().add(key, userId.toString());
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
    
    /**
     * 获取今日活跃用户数
     */
    public Long getTodayActiveUsers() {
        String today = LocalDate.now().toString();
        String key = "active_users:" + today;
        return redisTemplate.opsForSet().size(key);
    }
    
    /**
     * 记录API调用次数
     */
    public void recordApiCall(String apiPath) {
        String key = "api_calls:" + apiPath;
        redisTemplate.opsForValue().increment(key);
        
        // 按小时统计
        String hourKey = "api_calls:" + apiPath + ":" + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH"));
        redisTemplate.opsForValue().increment(hourKey);
        redisTemplate.expire(hourKey, 7, TimeUnit.DAYS);
    }
}
```

### 2. 实时统计

```java
@Service
public class RealTimeStatistics {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 使用HyperLogLog统计UV
     */
    public void recordUniqueVisitor(String page, String userId) {
        String key = "uv:" + page + ":" + LocalDate.now().toString();
        redisTemplate.opsForHyperLogLog().add(key, userId);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
    
    /**
     * 获取页面UV
     */
    public Long getUniqueVisitors(String page) {
        String key = "uv:" + page + ":" + LocalDate.now().toString();
        return redisTemplate.opsForHyperLogLog().size(key);
    }
    
    /**
     * 使用Bitmap统计用户签到
     */
    public void recordUserSignIn(Long userId) {
        String key = "signin:" + LocalDate.now().toString();
        redisTemplate.opsForValue().setBit(key, userId, true);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
    
    /**
     * 检查用户是否签到
     */
    public Boolean hasUserSignedIn(Long userId) {
        String key = "signin:" + LocalDate.now().toString();
        return redisTemplate.opsForValue().getBit(key, userId);
    }
}
```

---

## 排行榜功能

### 1. 基于Sorted Set的排行榜

```java
@Service
public class RankingService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String RANKING_KEY = "user_score_ranking";
    
    /**
     * 更新用户分数
     */
    public void updateUserScore(Long userId, double score) {
        redisTemplate.opsForZSet().add(RANKING_KEY, userId.toString(), score);
    }
    
    /**
     * 增加用户分数
     */
    public void incrementUserScore(Long userId, double increment) {
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, userId.toString(), increment);
    }
    
    /**
     * 获取用户排名（从1开始）
     */
    public Long getUserRank(Long userId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(RANKING_KEY, userId.toString());
        return rank != null ? rank + 1 : null;
    }
    
    /**
     * 获取用户分数
     */
    public Double getUserScore(Long userId) {
        return redisTemplate.opsForZSet().score(RANKING_KEY, userId.toString());
    }
    
    /**
     * 获取排行榜前N名
     */
    public List<RankingItem> getTopRanking(int count) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = 
                redisTemplate.opsForZSet().reverseRangeWithScores(RANKING_KEY, 0, count - 1);
        
        List<RankingItem> ranking = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
            RankingItem item = new RankingItem();
            item.setUserId(Long.valueOf(tuple.getValue().toString()));
            item.setScore(tuple.getScore());
            item.setRank(rank++);
            ranking.add(item);
        }
        return ranking;
    }
    
    /**
     * 获取指定范围的排行榜
     */
    public List<RankingItem> getRankingByRange(long start, long end) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = 
                redisTemplate.opsForZSet().reverseRangeWithScores(RANKING_KEY, start, end);
        
        List<RankingItem> ranking = new ArrayList<>();
        long rank = start + 1;
        for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
            RankingItem item = new RankingItem();
            item.setUserId(Long.valueOf(tuple.getValue().toString()));
            item.setScore(tuple.getScore());
            item.setRank(rank++);
            ranking.add(item);
        }
        return ranking;
    }
}

@Data
public class RankingItem {
    private Long userId;
    private Double score;
    private Long rank;
}
```

### 2. 多维度排行榜

```java
@Service
public class MultiDimensionRanking {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 按周排行榜
     */
    public void updateWeeklyScore(Long userId, double score) {
        String weekKey = "weekly_ranking:" + getWeekOfYear();
        redisTemplate.opsForZSet().add(weekKey, userId.toString(), score);
        redisTemplate.expire(weekKey, 8, TimeUnit.DAYS);
    }
    
    /**
     * 按月排行榜
     */
    public void updateMonthlyScore(Long userId, double score) {
        String monthKey = "monthly_ranking:" + YearMonth.now().toString();
        redisTemplate.opsForZSet().add(monthKey, userId.toString(), score);
        redisTemplate.expire(monthKey, 32, TimeUnit.DAYS);
    }
    
    /**
     * 分类排行榜
     */
    public void updateCategoryScore(String category, Long userId, double score) {
        String categoryKey = "category_ranking:" + category;
        redisTemplate.opsForZSet().add(categoryKey, userId.toString(), score);
    }
    
    private String getWeekOfYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-ww"));
    }
}
```

---

## 验证码场景详解

### 1. 手机短信验证码

#### 1.1 短信验证码服务

```java
@Service
public class SmsVerificationService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private SmsService smsService; // 短信发送服务
    
    private static final String SMS_CODE_PREFIX = "sms_code:";
    private static final String SMS_LIMIT_PREFIX = "sms_limit:";
    private static final int CODE_EXPIRE_TIME = 300; // 5分钟
    private static final int SEND_LIMIT_TIME = 60; // 1分钟内不能重复发送
    private static final int DAILY_LIMIT = 10; // 每日发送限制
    
    /**
     * 发送短信验证码
     */
    public void sendSmsCode(String phone, SmsType type) {
        // 1. 检查发送频率限制
        checkSendLimit(phone);
        
        // 2. 检查每日发送限制
        checkDailyLimit(phone);
        
        // 3. 生成验证码
        String code = generateCode();
        
        // 4. 存储验证码到Redis
        String codeKey = SMS_CODE_PREFIX + type.name().toLowerCase() + ":" + phone;
        redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 5. 设置发送频率限制
        String limitKey = SMS_LIMIT_PREFIX + phone;
        redisTemplate.opsForValue().set(limitKey, "1", SEND_LIMIT_TIME, TimeUnit.SECONDS);
        
        // 6. 记录每日发送次数
        String dailyKey = SMS_LIMIT_PREFIX + "daily:" + phone + ":" + LocalDate.now().toString();
        redisTemplate.opsForValue().increment(dailyKey);
        redisTemplate.expire(dailyKey, 24, TimeUnit.HOURS);
        
        // 7. 发送短信
        smsService.sendSms(phone, code, type);
        
        log.info("短信验证码已发送，手机号：{}，类型：{}", phone, type);
    }
    
    /**
     * 验证短信验证码
     */
    public boolean verifySmsCode(String phone, String code, SmsType type) {
        String codeKey = SMS_CODE_PREFIX + type.name().toLowerCase() + ":" + phone;
        String storedCode = (String) redisTemplate.opsForValue().get(codeKey);
        
        if (storedCode == null) {
            throw new BusinessException("验证码已过期或不存在");
        }
        
        if (!storedCode.equals(code)) {
            // 记录验证失败次数，防止暴力破解
            recordVerifyFailure(phone);
            return false;
        }
        
        // 验证成功后删除验证码
        redisTemplate.delete(codeKey);
        
        // 清除失败记录
        clearVerifyFailure(phone);
        
        return true;
    }
    
    /**
     * 检查发送频率限制
     */
    private void checkSendLimit(String phone) {
        String limitKey = SMS_LIMIT_PREFIX + phone;
        if (redisTemplate.hasKey(limitKey)) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }
    }
    
    /**
     * 检查每日发送限制
     */
    private void checkDailyLimit(String phone) {
        String dailyKey = SMS_LIMIT_PREFIX + "daily:" + phone + ":" + LocalDate.now().toString();
        Integer count = (Integer) redisTemplate.opsForValue().get(dailyKey);
        if (count != null && count >= DAILY_LIMIT) {
            throw new BusinessException("今日发送次数已达上限");
        }
    }
    
    /**
     * 记录验证失败次数
     */
    private void recordVerifyFailure(String phone) {
        String failKey = SMS_LIMIT_PREFIX + "fail:" + phone;
        Long failCount = redisTemplate.opsForValue().increment(failKey);
        redisTemplate.expire(failKey, 1, TimeUnit.HOURS);
        
        if (failCount >= 5) {
            throw new BusinessException("验证失败次数过多，请1小时后再试");
        }
    }
    
    /**
     * 清除验证失败记录
     */
    private void clearVerifyFailure(String phone) {
        String failKey = SMS_LIMIT_PREFIX + "fail:" + phone;
        redisTemplate.delete(failKey);
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}

public enum SmsType {
    LOGIN,      // 登录验证
    REGISTER,   // 注册验证
    RESET_PASSWORD, // 重置密码
    BIND_PHONE, // 绑定手机
    CHANGE_PHONE // 更换手机
}
```

#### 1.2 短信验证码控制器

```java
@RestController
@RequestMapping("/api/sms")
public class SmsController {
    
    @Autowired
    private SmsVerificationService smsVerificationService;
    
    /**
     * 发送短信验证码
     */
    @PostMapping("/send")
    public ApiResponse<Void> sendSmsCode(@RequestBody @Valid SmsCodeRequest request) {
        smsVerificationService.sendSmsCode(request.getPhone(), request.getType());
        return ApiResponse.success("验证码发送成功");
    }
    
    /**
     * 验证短信验证码
     */
    @PostMapping("/verify")
    public ApiResponse<Boolean> verifySmsCode(@RequestBody @Valid SmsVerifyRequest request) {
        boolean result = smsVerificationService.verifySmsCode(
                request.getPhone(), request.getCode(), request.getType());
        return ApiResponse.success(result);
    }
}

@Data
public class SmsCodeRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotNull(message = "短信类型不能为空")
    private SmsType type;
}

@Data
public class SmsVerifyRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码格式不正确")
    private String code;
    
    @NotNull(message = "短信类型不能为空")
    private SmsType type;
}
```

### 2. 图形验证码

#### 2.1 图形验证码服务

```java
@Service
public class CaptchaService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final int CAPTCHA_EXPIRE_TIME = 300; // 5分钟
    private static final int CAPTCHA_WIDTH = 120;
    private static final int CAPTCHA_HEIGHT = 40;
    
    /**
     * 生成图形验证码
     */
    public CaptchaResult generateCaptcha() {
        // 1. 生成验证码文本
        String code = generateCaptchaText();
        
        // 2. 生成唯一标识
        String captchaId = UUID.randomUUID().toString();
        
        // 3. 存储验证码到Redis
        String key = CAPTCHA_PREFIX + captchaId;
        redisTemplate.opsForValue().set(key, code.toLowerCase(), CAPTCHA_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 4. 生成验证码图片
        String imageBase64 = generateCaptchaImage(code);
        
        return new CaptchaResult(captchaId, imageBase64);
    }
    
    /**
     * 验证图形验证码
     */
    public boolean verifyCaptcha(String captchaId, String code) {
        if (StringUtils.isEmpty(captchaId) || StringUtils.isEmpty(code)) {
            return false;
        }
        
        String key = CAPTCHA_PREFIX + captchaId;
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            return false;
        }
        
        // 验证成功后删除验证码（一次性使用）
        redisTemplate.delete(key);
        
        return storedCode.equals(code.toLowerCase());
    }
    
    /**
     * 生成验证码文本
     */
    private String generateCaptchaText() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
    
    /**
     * 生成验证码图片
     */
    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        
        // 绘制干扰线
        Random random = new Random();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(CAPTCHA_WIDTH);
            int y1 = random.nextInt(CAPTCHA_HEIGHT);
            int x2 = random.nextInt(CAPTCHA_WIDTH);
            int y2 = random.nextInt(CAPTCHA_HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
        
        // 绘制验证码字符
        g.setFont(new Font("Arial", Font.BOLD, 20));
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            
            // 随机位置和角度
            int x = 20 + i * 20 + random.nextInt(10);
            int y = 25 + random.nextInt(10);
            
            // 旋转字符
            double angle = (random.nextDouble() - 0.5) * 0.5;
            g.rotate(angle, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.rotate(-angle, x, y);
        }
        
        // 添加噪点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(CAPTCHA_WIDTH);
            int y = random.nextInt(CAPTCHA_HEIGHT);
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillOval(x, y, 1, 1);
        }
        
        g.dispose();
        
        // 转换为Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
}

@Data
@AllArgsConstructor
public class CaptchaResult {
    private String captchaId;
    private String imageBase64;
}
```

#### 2.2 图形验证码控制器

```java
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    
    @Autowired
    private CaptchaService captchaService;
    
    /**
     * 生成图形验证码
     */
    @GetMapping("/generate")
    public ApiResponse<CaptchaResult> generateCaptcha() {
        CaptchaResult result = captchaService.generateCaptcha();
        return ApiResponse.success(result);
    }
    
    /**
     * 验证图形验证码
     */
    @PostMapping("/verify")
    public ApiResponse<Boolean> verifyCaptcha(@RequestBody @Valid CaptchaVerifyRequest request) {
        boolean result = captchaService.verifyCaptcha(request.getCaptchaId(), request.getCode());
        return ApiResponse.success(result);
    }
}

@Data
public class CaptchaVerifyRequest {
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
    
    @NotBlank(message = "验证码不能为空")
    private String code;
}
```

### 3. 验证码在登录流程中的应用

#### 3.1 登录服务集成验证码

```java
@Service
public class AuthService {
    
    @Autowired
    private CaptchaService captchaService;
    
    @Autowired
    private SmsVerificationService smsVerificationService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户名密码登录（需要图形验证码）
     */
    public LoginResult loginWithPassword(PasswordLoginRequest request) {
        // 1. 验证图形验证码
        if (!captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptchaCode())) {
            throw new BusinessException("图形验证码错误");
        }
        
        // 2. 验证用户名密码
        User user = userService.validateUser(request.getUsername(), request.getPassword());
        
        // 3. 生成JWT Token
        String token = jwtService.generateToken(user);
        
        return new LoginResult(token, user);
    }
    
    /**
     * 手机号短信登录
     */
    public LoginResult loginWithSms(SmsLoginRequest request) {
        // 1. 验证短信验证码
        if (!smsVerificationService.verifySmsCode(request.getPhone(), request.getSmsCode(), SmsType.LOGIN)) {
            throw new BusinessException("短信验证码错误");
        }
        
        // 2. 根据手机号查找用户
        User user = userService.findByPhone(request.getPhone());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 3. 生成JWT Token
        String token = jwtService.generateToken(user);
        
        return new LoginResult(token, user);
    }
}

@Data
public class PasswordLoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
    
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
}

@Data
public class SmsLoginRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "短信验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码格式不正确")
    private String smsCode;
}
```

### 4. 验证码最佳实践

#### 4.1 安全策略

```java
@Component
public class VerificationSecurityStrategy {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String IP_LIMIT_PREFIX = "ip_limit:";
    private static final String USER_LIMIT_PREFIX = "user_limit:";
    
    /**
     * IP限制策略
     */
    public void checkIpLimit(String ip, String action) {
        String key = IP_LIMIT_PREFIX + action + ":" + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        
        // 每小时限制100次
        if (count > 100) {
            throw new BusinessException("操作过于频繁，请稍后再试");
        }
    }
    
    /**
     * 用户限制策略
     */
    public void checkUserLimit(String userId, String action) {
        String key = USER_LIMIT_PREFIX + action + ":" + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        
        // 每小时限制20次
        if (count > 20) {
            throw new BusinessException("操作过于频繁，请稍后再试");
        }
    }
    
    /**
     * 滑动窗口限流
     */
    public boolean slidingWindowLimit(String key, int limit, int windowSize) {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize * 1000L;
        
        // 移除过期的记录
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
        
        // 获取当前窗口内的请求数
        Long count = redisTemplate.opsForZSet().count(key, windowStart, now);
        
        if (count >= limit) {
            return false;
        }
        
        // 添加当前请求
        redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), now);
        redisTemplate.expire(key, windowSize, TimeUnit.SECONDS);
        
        return true;
    }
}
```

#### 4.2 监控和统计

```java
@Service
public class VerificationMonitorService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 记录验证码发送统计
     */
    public void recordSendStatistics(String type, boolean success) {
        String date = LocalDate.now().toString();
        String successKey = "verification_stats:" + type + ":success:" + date;
        String totalKey = "verification_stats:" + type + ":total:" + date;
        
        redisTemplate.opsForValue().increment(totalKey);
        if (success) {
            redisTemplate.opsForValue().increment(successKey);
        }
        
        redisTemplate.expire(successKey, 30, TimeUnit.DAYS);
        redisTemplate.expire(totalKey, 30, TimeUnit.DAYS);
    }
    
    /**
     * 记录验证码验证统计
     */
    public void recordVerifyStatistics(String type, boolean success) {
        String date = LocalDate.now().toString();
        String successKey = "verification_verify:" + type + ":success:" + date;
        String totalKey = "verification_verify:" + type + ":total:" + date;
        
        redisTemplate.opsForValue().increment(totalKey);
        if (success) {
            redisTemplate.opsForValue().increment(successKey);
        }
        
        redisTemplate.expire(successKey, 30, TimeUnit.DAYS);
        redisTemplate.expire(totalKey, 30, TimeUnit.DAYS);
    }
    
    /**
     * 获取验证码统计信息
     */
    public VerificationStats getVerificationStats(String type, String date) {
        String sendSuccessKey = "verification_stats:" + type + ":success:" + date;
        String sendTotalKey = "verification_stats:" + type + ":total:" + date;
        String verifySuccessKey = "verification_verify:" + type + ":success:" + date;
        String verifyTotalKey = "verification_verify:" + type + ":total:" + date;
        
        Integer sendSuccess = (Integer) redisTemplate.opsForValue().get(sendSuccessKey);
        Integer sendTotal = (Integer) redisTemplate.opsForValue().get(sendTotalKey);
        Integer verifySuccess = (Integer) redisTemplate.opsForValue().get(verifySuccessKey);
        Integer verifyTotal = (Integer) redisTemplate.opsForValue().get(verifyTotalKey);
        
        return VerificationStats.builder()
                .sendSuccess(sendSuccess != null ? sendSuccess : 0)
                .sendTotal(sendTotal != null ? sendTotal : 0)
                .verifySuccess(verifySuccess != null ? verifySuccess : 0)
                .verifyTotal(verifyTotal != null ? verifyTotal : 0)
                .build();
    }
}

@Data
@Builder
public class VerificationStats {
    private Integer sendSuccess;
    private Integer sendTotal;
    private Integer verifySuccess;
    private Integer verifyTotal;
    
    public Double getSendSuccessRate() {
        return sendTotal > 0 ? (double) sendSuccess / sendTotal : 0.0;
    }
    
    public Double getVerifySuccessRate() {
        return verifyTotal > 0 ? (double) verifySuccess / verifyTotal : 0.0;
    }
}
```

## 总结

Redis在SpringBoot项目中的应用非常广泛，主要包括：

1. **缓存功能**：提高数据访问速度，减少数据库压力
2. **分布式锁**：解决分布式环境下的并发问题
3. **会话管理**：实现分布式会话存储
4. **消息队列**：实现异步消息处理
5. **限流功能**：防止系统过载
6. **数据统计**：实时统计和分析
7. **排行榜功能**：高效的排序和排名
8. **验证码场景**：安全验证和防刷机制

在验证码场景中，Redis的作用尤为重要：
- **存储验证码**：设置合适的过期时间
- **频率限制**：防止恶意刷取验证码
- **失败统计**：防止暴力破解
- **监控统计**：分析验证码使用情况

通过合理使用Redis的各种数据结构和功能，可以大大提升系统的性能、安全性和用户体验。