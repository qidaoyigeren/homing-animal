package org.example.hominganimal.infrastructure.persistence.converter;

import org.example.hominganimal.domain.device.entity.Device;
import org.example.hominganimal.domain.device.valueobject.DeviceStatus;
import org.example.hominganimal.infrastructure.persistence.dataobject.DeviceDO;

/**
 * 设备DO<->Domain Entity转换器
 */
public class DeviceConverter {
    public static DeviceDO toDO(Device d) {
        if(d==null)return null;
        DeviceDO ddo=new DeviceDO();
        ddo.setId(d.getId());
        ddo.setDeviceSerial(d.getDeviceSerial());
        ddo.setValidateCode(d.getValidateCode());
        ddo.setDeviceName(d.getDeviceName());
        ddo.setStatus(d.getStatus().getCode());
        ddo.setIsPetDetectOpen(d.isPetDetectOpen() ? 1 : 0);
        ddo.setUserId(d.getUserId());
        ddo.setLastCaptureUrl(d.getLastCaptureUrl());
        ddo.setLastCaptureTime(d.getLastCaptureTime());
        return ddo;
    }
    public static Device toEntity(DeviceDO d) {
        if (d == null) return null;
        Device device=Device.create(d.getDeviceSerial(),d.getValidateCode(),d.getDeviceName(),d.getUserId());
        device.setId(d.getId());
        device.convertPetDetect(d.getIsPetDetectOpen() == 1);
        device.setStatus(d.getStatus()==1?DeviceStatus.ONLINE:DeviceStatus.OFFLINE);
        device.setCreateTime(d.getCreateTime());
        if (d.getLastCaptureUrl() != null) {
            device.updateCapture(d.getLastCaptureUrl());
        }
        return device;
    }
}
