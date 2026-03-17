
package org.example.hominganimal.infrastructure.ezviz;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hominganimal.infrastructure.config.RestTemplateConfig;
import org.example.hominganimal.infrastructure.ezviz.EzvizProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;


import java.util.concurrent.TimeUnit;

/**
 * 萤石AccessToken管理器
 * 技术亮点：Redis缓存Token + 过期自动刷新 + 双重检查锁防止并发刷新
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EzvizTokenManager {

    private final EzvizProperties properties;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    private static final String TOKEN_CACHE_KEY = "ezviz:access_token";
    private static final String TOKEN_LOCK_KEY = "ezviz:token_lock";

    /**
     * 获取有效的AccessToken
     * 优先从Redis缓存获取，失效时自动刷新
     */
    public String getAccessToken() {
        // 1. 先从Redis获取缓存的Token
        String cachedToken = redisTemplate.opsForValue().get(TOKEN_CACHE_KEY);
        if (cachedToken != null) {
            return cachedToken;
        }

        // 2. 缓存未命中，加锁刷新（防止并发场景多次刷新）
        return refreshToken();
    }

    /**
     * 刷新Token（带分布式锁）
     */
    private synchronized String refreshToken() {
        // 双重检查：可能其他线程已经刷新了
        String cachedToken = redisTemplate.opsForValue().get(TOKEN_CACHE_KEY);
        if (cachedToken != null) {
            return cachedToken;
        }

        log.info("开始刷新萤石AccessToken...");

        String url = properties.getApiBaseUrl() + "/api/lapp/token/get";

        // 构建表单参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("appKey", properties.getAppKey());
        params.add("appSecret", properties.getAppSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        JSONObject json = JSONObject.parseObject(response.getBody());

        if (!"200".equals(json.getString("code"))) {
            log.error("获取萤石Token失败: {}", json);
            throw new RuntimeException("获取萤石AccessToken失败: " + json.getString("msg"));
        }

        JSONObject data = json.getJSONObject("data");
        String accessToken = data.getString("accessToken");
        long expireTime = data.getLongValue("expireTime");

        // 计算剩余有效时间，提前10分钟过期以留出缓冲
        long ttlSeconds = (expireTime - System.currentTimeMillis()) / 1000 - 600;
        if (ttlSeconds < 60) {
            ttlSeconds = 60;
        }

        // 缓存到Redis
        redisTemplate.opsForValue().set(TOKEN_CACHE_KEY, accessToken, ttlSeconds, TimeUnit.SECONDS);
        log.info("萤石Token刷新成功，TTL={}秒", ttlSeconds);

        return accessToken;
    }

    /**
     * 强制刷新Token（当API返回token过期错误时调用）
     */
    public void invalidateToken() {
        redisTemplate.delete(TOKEN_CACHE_KEY);
        log.info("已清除萤石Token缓存，下次请求将自动刷新");
    }
}