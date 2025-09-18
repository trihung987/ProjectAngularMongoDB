package me.trihung.exception;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnauthorizedException extends RuntimeException {
    private Integer code;
    private String message;

    public UnauthorizedException(String message) {
        this.code = 401;
        this.message = message;
    }

    public static UnauthorizedException defaultException() {
        return UnauthorizedException.builder()
                .code(401)
                .message("Unauthorized| Không xác thực")
                .build();
    }

    public static UnauthorizedException message(String message) {
        return UnauthorizedException.builder()
                .code(401)
                .message(message)
                .build();
    }
}
