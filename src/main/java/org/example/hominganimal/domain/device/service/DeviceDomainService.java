package org.example.hominganimal.domain.device.service;

import org.example.hominganimal.domain.device.repository.DeviceRepository;
import org.example.hominganimal.domain.shared.exception.BusinessException;
import org.example.hominganimal.domain.shared.exception.ErrorCode;

/**
 * 设备领域服务
 * 包含跨实体的业务规则（如绑定上限校验）
 */
public class DeviceDomainService {

    private static final int MAX_DEVICE_PER_USER = 10;

    /**
     * 校验用户是否可以绑定新设备
     */
    public void validateBindDevice(DeviceRepository repository, Long userId) {
        int count = repository.findByUserId(userId).size();
        if (count >= MAX_DEVICE_PER_USER) {
            throw new BusinessException(ErrorCode.DEVICE_BINDLIMIT,
                    "每个用户最多绑定" + MAX_DEVICE_PER_USER + "台设备");
        }
    }
}