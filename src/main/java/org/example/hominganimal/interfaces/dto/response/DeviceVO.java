package org.example.hominganimal.interfaces.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceVO {
    private Long id;
    private String deviceSerial;
    private String deviceName;
    private Integer status;
    private String statusDesc;
    private Boolean isPetDetectOpen;
    private String lastCaptureUrl;
    private LocalDateTime lastCaptureTime;
    private LocalDateTime createTime;
}
