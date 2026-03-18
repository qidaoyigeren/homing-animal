package org.example.hominganimal.interfaces.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoListVO {
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Long fileSize;
}
