package org.example.hominganimal.application.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hominganimal.domain.device.entity.Device;
import org.example.hominganimal.domain.device.service.DeviceDomainService;
import org.example.hominganimal.domain.shared.exception.BusinessException;
import org.example.hominganimal.domain.shared.exception.ErrorCode;
import org.example.hominganimal.infrastructure.ezviz.EzvizApiClient;
import org.example.hominganimal.infrastructure.persistence.repository.DeviceRepositoryImpl;
import org.example.hominganimal.interfaces.dto.response.VideoListVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceAppService {
    private final EzvizApiClient ezvizApiClient;
    private final DeviceRepositoryImpl deviceRepository;
    private final DeviceDomainService domainService = new DeviceDomainService();
    @Transactional
    public Device bind(
            Long currentUserId,
            @NotBlank(message = "设备序列号不能为空") String deviceSerial,
            @NotBlank(message = "验证码不能为空") String validateCode,
            String deviceName) {
        //检查绑定上限
        domainService.validateBindDevice(deviceRepository, currentUserId);
        //检查设备是否已经绑定
        deviceRepository.findBySerial(deviceSerial).ifPresent(d->{
            throw new BusinessException(ErrorCode.DEVICE_ALREADY_BOUND);
        });
        //调用萤石云API进行设备绑定
        ezvizApiClient.addDevice(deviceSerial, validateCode);
        //在持久层保存数据
        Device device=Device.create(deviceSerial,validateCode, deviceName, currentUserId);
        deviceRepository.save(device);
        return device;
    }
    @Transactional
    public void unBind(Long currentUserId, Long deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(()->new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        ezvizApiClient.deleteDevice(device.getDeviceSerial());
        deviceRepository.deleteByDeviceId(deviceId);
        log.info("设备解绑成功: userId={}, serial={}", currentUserId, device.getDeviceSerial());

    }
    @Transactional
    public List<Device> list(Long userId) {
        return deviceRepository.findByUserId(userId);
    }
    @Transactional
    public void petDetect(Long currentUserId, Long deviceId, Boolean open) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(()->new BusinessException(ErrorCode.DEVICE_NOT_FOUND));
        device.convertPetDetect(open);
        deviceRepository.update(device);
    }
    @Transactional
    public String getUrlBySerial(String deviceSerial) {
        return ezvizApiClient.getUrlBySerial(deviceSerial);
    }
    @Transactional
    public String getBackUrlBySerial(String deviceSerial, String startTime, String endTime) {
        return ezvizApiClient.getBackUrlBySerial(deviceSerial,startTime,endTime);
    }
    @Transactional
    public List<VideoListVO> getVideoListByTime(LocalDateTime startTime, LocalDateTime endTime, String deviceSerial) {
        return ezvizApiClient.getVideoListByTime(startTime,endTime,deviceSerial);
    }
}
