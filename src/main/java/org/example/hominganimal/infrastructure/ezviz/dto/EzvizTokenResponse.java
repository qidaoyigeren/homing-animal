package org.example.hominganimal.infrastructure.ezviz.dto;
import lombok.Data;

@Data
public class EzvizTokenResponse {
    private String accessToken;
    private Long expireTime;
}