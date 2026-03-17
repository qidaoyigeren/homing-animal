package org.example.hominganimal.infrastructure.ezviz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 萤石开放平台API客户端
 * 封装所有与萤石云的HTTP交互，统一处理Token和错误重试
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EzvizApiClient {
    private final EzvizProperties properties;
    private final EzvizTokenManager tokenManager;
    private final RestTemplate restTemplate;

    // ===================== 设备管理 =====================
    public void addDevice(String deviceSerial, String validateCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("accessToken", tokenManager.getAccessToken());
        params.add("deviceSerial", deviceSerial);
        params.add("validateCode", validateCode);
        JSONObject result = doPost("/api/lapp/device/add", params);
        log.info("添加设备成功: serial={}", deviceSerial);
    }
    private JSONObject doPost(String path, MultiValueMap<String, String> params) {
        String url= properties.getApiBaseUrl()+path;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        JSONObject json = JSONObject.parseObject(response.getBody());
        String code=json.getString("code");
        if("10002".equals(code)){
            log.info("Token过期，尝试刷新");
            tokenManager.invalidateToken();
            params.set("accessToken", tokenManager.getAccessToken());
            request = new HttpEntity<>(params, headers);
            response = restTemplate.postForEntity(url, request, String.class);
            json = JSONObject.parseObject(response.getBody());
            code=json.getString("code");
        }
        if(!"200".equals(code)){
            log.error("萤石API调用失败: url={}, code={}, msg={}", path, code, json.getString("msg"));
            throw new RuntimeException("萤石API调用失败[" + path + "]: " + json.getString("msg"));
        }
        return json;
    }
}
