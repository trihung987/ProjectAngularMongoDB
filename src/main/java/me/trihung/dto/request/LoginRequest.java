package me.trihung.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Vui lòng nhập username")
    private String username;
    
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;
}