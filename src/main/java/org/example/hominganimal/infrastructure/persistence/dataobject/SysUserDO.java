package org.example.hominganimal.infrastructure.persistence.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;   // BCrypt 加密后的密文
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
