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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
