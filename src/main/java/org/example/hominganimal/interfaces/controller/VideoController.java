package org.example.hominganimal.interfaces.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hominganimal.application.service.DeviceAppService;
import org.example.hominganimal.interfaces.dto.response.ApiResponse;
import org.example.hominganimal.interfaces.dto.response.VideoListVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/video")
public class VideoController {
    private final DeviceAppService deviceAppService;
    @GetMapping("/live/{deviceSerial}")
    public ApiResponse<Map<String,String>>getVideoUrl(@PathVariable String deviceSerial){
        String url=deviceAppService.getUrlBySerial(deviceSerial);
        return ApiResponse.ok(Map.of("url",url));
    }
    @GetMapping("/playback/{deviceSerial}")
    public ApiResponse<Map<String,String>>getBackUrl(
            @PathVariable String deviceSerial,
            @RequestParam String startTime,
            @RequestParam String endTime
    ){
        String url=deviceAppService.getBackUrlBySerial(deviceSerial,startTime,endTime);
        return ApiResponse.ok(Map.of("url",url));
    }
    @GetMapping("/by/time")
    public ApiResponse<List<VideoListVO>> getVideoListByTime(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime,
            @RequestParam String deviceSerial
    ) {
        List<VideoListVO>list= deviceAppService.getVideoListByTime(startTime,endTime,deviceSerial);
        return ApiResponse.ok(list);
    }
}
