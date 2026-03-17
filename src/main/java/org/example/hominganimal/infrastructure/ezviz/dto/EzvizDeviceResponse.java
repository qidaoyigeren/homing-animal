package org.example.hominganimal.infrastructure.ezviz.dto;

import lombok.Data;

@Data
public class EzvizDeviceResponse {
    private String deviceSerial;
    private String deviceName;
    private Integer status;  // 1-在线, 2-离线
}