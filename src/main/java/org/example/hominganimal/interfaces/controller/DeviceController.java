package org.example.hominganimal.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.example.hominganimal.application.service.DeviceAppService;
import org.example.hominganimal.infrastructure.persistence.dataobject.DeviceDO;
import org.example.hominganimal.interfaces.dto.request.DeviceBindRequest;
import org.example.hominganimal.interfaces.dto.response.ApiResponse;
import org.example.hominganimal.interfaces.dto.response.DeviceVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceAppService deviceService;
    @PostMapping("/bind")
    public ApiResponse<DeviceVO>bind(DeviceBindRequest deviceBindRequest){
        return null;
    }
}
