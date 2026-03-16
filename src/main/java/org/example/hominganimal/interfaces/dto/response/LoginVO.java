package org.example.hominganimal.interfaces.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Long userId;
    private String username;
    private String nickname;

    public LoginVO(String token, Long expiresIn, Long userId, String username, String nickname) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
    }
}