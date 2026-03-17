package org.example.hominganimal.application.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hominganimal.domain.device.entity.Device;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceAppService {
    public Device bind(Long currentUserId, @NotBlank(message = "设备序列号不能为空") String deviceSerial, @NotBlank(message = "验证码不能为空") String validateCode, String deviceName) {

        return null;
    }
}
