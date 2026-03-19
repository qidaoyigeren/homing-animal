package org.example.hominganimal.infrastructure.ezviz;

import com.alibaba.fastjson2.JSONArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson2.JSONObject;
import org.example.hominganimal.infrastructure.ezviz.dto.EzvizDeviceResponse;
import org.example.hominganimal.infrastructure.utils.TimeUtil;
import org.example.hominganimal.interfaces.dto.response.VideoListVO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public void deleteDevice(String deviceSerial) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("accessToken", tokenManager.getAccessToken());
        params.add("deviceSerial", deviceSerial);
        JSONObject result = doPost("/api/lapp/device/delete", params);
        log.info("删除设备成功: serial={}", deviceSerial);
    }
    public List<EzvizDeviceResponse> getDeviceList(int pageStart,int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("accessToken", tokenManager.getAccessToken());
        params.add("pageStart", String.valueOf(pageStart));
        params.add("pageSize", String.valueOf(pageSize));
        JSONObject result = doPost("/api/lapp/device/list", params);
        JSONArray deviceArray = result.getJSONArray("data");
        List<EzvizDeviceResponse> devices = new ArrayList<>();
        if(deviceArray!=null){
            for(int i=0;i<deviceArray.size();i++){
                JSONObject item = deviceArray.getJSONObject(i);
                EzvizDeviceResponse device = new EzvizDeviceResponse();
                device.setDeviceSerial(item.getString("deviceSerial"));
                device.setDeviceName(item.getString("deviceName"));
                device.setStatus(item.getIntValue("status"));
                devices.add(device);
            }
        }
        return devices;
    }
    public int getDeviceStatus(String deviceSerial) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("accessToken", tokenManager.getAccessToken());
        params.add("deviceSerial", deviceSerial);

        JSONObject result = doPost("/api/lapp/device/status/get", params);
        // 萤石返回的 status: 1-在线, 2-离线
        JSONArray dataArray = result.getJSONArray("data");
        if (dataArray != null && !dataArray.isEmpty()) {
            return dataArray.getJSONObject(0).getIntValue("status");
        }
        return 0;
    }
    // ===================== 视频能力 =====================

    public String getUrlBySerial(String deviceSerial) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("accessToken", tokenManager.getAccessToken());
        params.add("deviceSerial", deviceSerial);
        params.add("channelNo", "1");
        params.add("protocol", "2"); // 2=HLS
        params.add("quality", "1");  // 1=高清
        JSONObject result = doPost("/api/lapp/v2/live/address/get", params);
        JSONObject data = result.getJSONObject("data");
        if(data!=null){
            return data.getString("url");
        }
        return null;
    }
    public String getBackUrlBySerial(String deviceSerial, String startTime, String endTime) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("accessToken", tokenManager.getAccessToken());
        params.add("deviceSerial", deviceSerial);
        params.add("channelNo", "1");
        params.add("protocol", "2"); // 2=HLS
        params.add("startTime", startTime);
        params.add("endTime", endTime);
        JSONObject result = doPost("/api/lapp/v2/cloud/record/url/get", params);
        JSONObject data = result.getJSONObject("data");
        if(data!=null)
            return data.getString("url");
        return null;
    }
    public List<VideoListVO> getVideoListByTime(LocalDateTime startTime, LocalDateTime endTime, String deviceSerial) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("accessToken", tokenManager.getAccessToken());
        params.add("deviceSerial", deviceSerial);
        params.add("channelNo", "1");
        params.add("startTime", TimeUtil.format(startTime));
        params.add("endTime", TimeUtil.format(endTime));
        JSONObject result = doPost("/api/lapp/video/by/time", params);
        JSONArray dataArray = result.getJSONArray("data");
        List<VideoListVO> list = new ArrayList<>();
        if(dataArray!=null){
            for(int i=0;i<dataArray.size();i++){
                JSONObject item = dataArray.getJSONObject(i);
                VideoListVO vo = new VideoListVO();
                vo.setBeginTime(TimeUtil.parseSafe(item.getString("startTime")));
                vo.setEndTime(TimeUtil.parseSafe(item.getString("endTime")));
                vo.setFileSize(item.getLongValue("fileSize"));
                list.add(vo);
            }
        }
        return list;
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
