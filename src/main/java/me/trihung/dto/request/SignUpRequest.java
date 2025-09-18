package me.trihung.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
	
    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    @Size(min = 5, max = 30, message = "Tên đăng nhập phải có độ dài từ 5-30 ký tự")
    private String username;
    
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số")
    private String password;
    
    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String retypedPassword;
    
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Vui lòng nhập email")
    private String email;
    
    @Size(min = 5, max = 50, message = "Họ tên phải có độ dài từ 5 đến 50 ký tự")
    @NotBlank(message = "Vui lòng nhập họ tên đầy đủ")
    private String fullname;
}