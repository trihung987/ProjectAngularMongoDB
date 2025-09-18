package me.trihung.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO{
    private UUID id;
    private String fullName;
    private String email;
    private String username;
//    private String phone;
    private String avatarUrl;
//    private String description;
//    private LocalDate birthday;
    //private Set<Role> roles = new HashSet<>();
}