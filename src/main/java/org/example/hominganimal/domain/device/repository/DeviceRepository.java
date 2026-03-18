package org.example.hominganimal.domain.device.repository;

import jakarta.validation.constraints.NotBlank;
import org.example.hominganimal.domain.device.entity.Device;

import java.util.List;
import java.util.Optional;

/**
 * 设备仓储接口（领域层定义，基础设施层实现）
 */
public interface DeviceRepository {
    List<Device> findByUserId(Long userId);
    Optional<Device> findBySerial(@NotBlank(message = "设备序列号不能为空") String deviceSerial);

    Device save(Device device);

    Optional<Device> findByDeviceId(Long deviceId);

    void deleteByDeviceId(Long deviceId);

    void update(Device device);
}
