
package org.example.hominganimal.infrastructure.ezviz;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private final RedissonClient redissonClient;

    private static final String TOKEN_CACHE_KEY = "ezviz:access_token";
    private static final String TOKEN_LOCK_KEY = "ezviz:token_refresh_lock";
    private static final long LOCK_WAIT_SECONDS    = 5;
    private static final long LOCK_LEASE_SECONDS   = 10;
    /**
     * 获取有效的AccessToken
     * 优先从Redis缓存获取，失效时自动刷新
     */
    public String getAccessToken() {
        //Redis缓存命中直接返回
        String cacheToken=redisTemplate.opsForValue().get(TOKEN_CACHE_KEY);
        if(cacheToken!=null){
            return cacheToken;
        }
        //加分布式锁刷新
        RLock lock = redissonClient.getLock(TOKEN_LOCK_KEY);
        try{
            boolean acquired = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if(!acquired){
                //等待超时还未拿到锁，说明其他节点正在刷新，稍后重试
                log.warn("获取Token分布式锁超时，尝试直接读取缓存");
                String fallback=redisTemplate.opsForValue().get(TOKEN_CACHE_KEY);
                if(fallback!=null)return fallback;
                throw new RuntimeException("获取Token失败:锁等待超时且缓存为空");
            }
            //双重检查，防止并发刷新
            String doubleCheck=redisTemplate.opsForValue().get(TOKEN_CACHE_KEY);
            if(doubleCheck!=null){
                log.debug("双重检查命中，直接使用已刷新Token");
                return doubleCheck;
            }
            return doRefreshToken();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取Token分布式锁被中断", e);
        }finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
    private String doRefreshToken(){
        log.info("Redission开始刷新萤石AccessToken");
        String url= properties.getApiBaseUrl()+"/api/lapp/token/get";
        MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
        params.add("appKey",properties.getAppKey());
        params.add("appSecret",properties.getAppSecret());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url, new HttpEntity<>(params, headers), String.class
        );
        JSONObject json = JSONObject.parseObject(response.getBody());
        if (!"200".equals(json.getString("code"))) {
            throw new RuntimeException("萤石Token刷新失败: " + json.getString("msg"));
        }
        JSONObject data=json.getJSONObject("data");
        String accessToken=data.getString("accessToken");
        long expireTime=data.getLong("expireTime");
        // 提前10分钟过期，留出缓冲时间
        long ttlSeconds = (expireTime - System.currentTimeMillis()) / 1000 - 600;
        ttlSeconds = Math.max(ttlSeconds, 60);
        redisTemplate.opsForValue().set(TOKEN_CACHE_KEY, accessToken, ttlSeconds, TimeUnit.SECONDS);
        log.info("[Redisson] 萤石Token刷新成功，TTL={}秒", ttlSeconds);
        return accessToken;
    }
    /**
     * 强制失效缓存（API返回Token过期时调用）
     */
    public void invalidateToken() {
        redisTemplate.delete(TOKEN_CACHE_KEY);
        log.info("已清除萤石Token缓存");
    }
}