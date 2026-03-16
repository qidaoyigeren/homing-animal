package org.example.hominganimal.infrastructure.persistence.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("device_info")
public class DeviceDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceSerial;
    private String validateCode;
    private String deviceName;
    private Integer status;
    private Integer isPetDetectOpen;
    private Long userId;
    private String lastCaptureUrl;
    private LocalDateTime lastCaptureTime;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
