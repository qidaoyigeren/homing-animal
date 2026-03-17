package org.example.hominganimal.interfaces.assembler;

import org.example.hominganimal.domain.device.entity.Device;
import org.example.hominganimal.domain.device.valueobject.DeviceStatus;
import org.example.hominganimal.interfaces.dto.response.DeviceVO;

import java.time.LocalDateTime;

public class DeviceAssembler {
    public static DeviceVO toVO(Device device){
        DeviceVO vo=new DeviceVO();
        vo.setId(device.getId());
        vo.setDeviceSerial(device.getDeviceSerial());
        vo.setDeviceName(device.getDeviceName());
        vo.setStatus(device.getStatus()== DeviceStatus.ONLINE?1:0);
        vo.setStatusDesc(device.getStatus().getDesc());
        vo.setIsPetDetectOpen(device.isPetDetectOpen());
        vo.setCreateTime(device.getCreateTime());
        vo.setLastCaptureUrl(device.getLastCaptureUrl());
        vo.setLastCaptureTime(device.getLastCaptureTime());
        return vo;
    }
}
