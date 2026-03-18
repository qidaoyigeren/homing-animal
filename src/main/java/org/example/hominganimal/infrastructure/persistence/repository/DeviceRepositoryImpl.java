package org.example.hominganimal.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.hominganimal.domain.device.entity.Device;
import org.example.hominganimal.domain.device.repository.DeviceRepository;
import org.example.hominganimal.infrastructure.persistence.converter.DeviceConverter;
import org.example.hominganimal.infrastructure.persistence.dataobject.DeviceDO;
import org.example.hominganimal.infrastructure.persistence.mapper.DeviceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepository {
    private final DeviceMapper deviceMapper;
    @Override
    public List<Device> findByUserId(Long userId){
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getUserId, userId);
        return deviceMapper.selectList(queryWrapper)
                .stream()
                .map(DeviceConverter::toEntity)
                .toList();
    }

    public Optional<Device> findBySerial(@NotBlank(message = "设备序列号不能为空") String deviceSerial) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSerial, deviceSerial);
        DeviceDO deviceDO = deviceMapper.selectOne(queryWrapper);
        return Optional.ofNullable(DeviceConverter.toEntity(deviceDO));
    }

    @Override
    public Device save(Device device) {
        DeviceDO ddo= DeviceConverter.toDO(device);
        deviceMapper.insert(ddo);
        device.setId(ddo.getId());
        return device;
    }

    @Override
    public Optional<Device> findByDeviceId(Long deviceId) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getId, deviceId);
        DeviceDO deviceDO = deviceMapper.selectOne(queryWrapper);
        return Optional.ofNullable(DeviceConverter.toEntity(deviceDO));
    }

    @Override
    public void deleteByDeviceId(Long deviceId) {
        deviceMapper.deleteById(deviceId);
    }

    @Override
    public void update(Device device) {
        deviceMapper.updateById(DeviceConverter.toDO(device));
    }


}
