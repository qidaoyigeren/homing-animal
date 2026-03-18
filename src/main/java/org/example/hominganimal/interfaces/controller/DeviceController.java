package org.example.hominganimal.interfaces.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hominganimal.application.service.DeviceAppService;
import org.example.hominganimal.domain.device.entity.Device;
import org.example.hominganimal.infrastructure.persistence.dataobject.DeviceDO;
import org.example.hominganimal.infrastructure.security.SecurityUtil;
import org.example.hominganimal.interfaces.assembler.DeviceAssembler;
import org.example.hominganimal.interfaces.dto.request.DeviceBindRequest;
import org.example.hominganimal.interfaces.dto.response.ApiResponse;
import org.example.hominganimal.interfaces.dto.response.DeviceVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceAppService deviceService;
    @PostMapping("/bind")
    public ApiResponse<DeviceVO>bind(@Valid@RequestBody DeviceBindRequest deviceBindRequest){
        Device device=deviceService
                .bind(SecurityUtil.getCurrentUserId(),
                        deviceBindRequest.getDeviceSerial(),
                        deviceBindRequest.getValidateCode(),
                        deviceBindRequest.getDeviceName());
        return ApiResponse.ok(DeviceAssembler.toVO(device));
    }
    @DeleteMapping("/{deviceId}")
    public ApiResponse<Void>unBind(@PathVariable Long deviceId) {
        deviceService.unBind(SecurityUtil.getCurrentUserId(), deviceId);
        return ApiResponse.ok();
    }
    @GetMapping("/list")
    public ApiResponse<List<DeviceVO>>list(){
        Long userId= SecurityUtil.getCurrentUserId();
        List<Device> devices=deviceService.list(userId);
        List<DeviceVO>deviceVOS= devices.stream().map(DeviceAssembler::toVO).toList();
        return ApiResponse.ok(deviceVOS);
    }
    @PutMapping("/{deviceId}/pet-detect")
    public ApiResponse<Void>petDetect(@PathVariable Long deviceId, @RequestParam Boolean open){
        deviceService.petDetect(SecurityUtil.getCurrentUserId(), deviceId, open);
        return ApiResponse.ok();
    }

}
