package org.example.hominganimal.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceBindRequest {
    @NotBlank(message = "设备序列号不能为空")
    private String deviceSerial;
    @NotBlank(message = "验证码不能为空")
    private String validateCode;
    private String deviceName;
}
