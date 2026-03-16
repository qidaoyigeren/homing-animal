package org.example.hominganimal.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.hominganimal.infrastructure.persistence.dataobject.SysUserDO;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserDO> {
}