package org.example.hominganimal.domain.device.entity;

import lombok.Data;
import org.example.hominganimal.domain.device.valueobject.DeviceStatus;

import java.time.LocalDateTime;
public class Device {
    private Long id;
    private String deviceSerial;
    private String validateCode;
    private String deviceName;
    private DeviceStatus status;
    private boolean isPetDetectOpen;
    private Long userId;
    private String lastCaptureUrl;
    private LocalDateTime lastCaptureTime;
    private LocalDateTime createTime;
    public static Device create(String deviceSerial, String validateCode, String deviceName, Long userId) {
        Device device = new Device();
        device.deviceSerial = deviceSerial.toUpperCase(); // 序列号统一大写
        device.validateCode = validateCode.toUpperCase();
        device.deviceName = deviceName;
        device.userId = userId;
        device.status = DeviceStatus.OFFLINE;
        device.isPetDetectOpen = true;
        device.createTime = LocalDateTime.now();
        return device;
    }
    //===== 业务行为 =====
    public boolean updateStatus(DeviceStatus status) {
        if(this.status!=status){
            this.status = status;
            return true;
        }
        return false;
    }
    public void convertPetDetect(Boolean detectStatus){
        this.isPetDetectOpen = detectStatus;
    }
    public void updateCapture(String imageUrl) {
        this.lastCaptureUrl = imageUrl;
        this.lastCaptureTime = LocalDateTime.now();
    }
    public Boolean isOnline(){
        return this.status == DeviceStatus.ONLINE;
    }
    /** 判断设备是否需要监测（在线且开启了宠物检测） */
    public boolean shouldMonitor() {
        return isOnline() && isPetDetectOpen;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceSerial() { return deviceSerial; }
    public String getValidateCode() { return validateCode; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public DeviceStatus getStatus() { return status; }
    public void setStatus(DeviceStatus status) { this.status = status; }
    public boolean isPetDetectOpen() { return isPetDetectOpen; }
    public Long getUserId() { return userId; }
    public String getLastCaptureUrl() { return lastCaptureUrl; }
    public LocalDateTime getLastCaptureTime() { return lastCaptureTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
