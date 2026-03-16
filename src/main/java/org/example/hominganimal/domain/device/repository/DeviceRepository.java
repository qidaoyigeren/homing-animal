package org.example.hominganimal.domain.device.repository;

import org.example.hominganimal.domain.device.entity.Device;

import java.util.List;

/**
 * 设备仓储接口（领域层定义，基础设施层实现）
 */
public interface DeviceRepository {
    List<Device> findByUserId(Long userId);
}
