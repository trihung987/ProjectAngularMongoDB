package me.trihung.service;

import me.trihung.dto.TokenDTO;
import me.trihung.dto.UserDTO;
import me.trihung.dto.request.LoginRequest;
import me.trihung.dto.request.RefreshTokenRequest;
import me.trihung.dto.request.SignUpRequest;

public interface UserService {
	void changePassword(String oldPassword, String newPassword);
	boolean userExists(String username);
	void signUp(SignUpRequest signUpRequest);
	TokenDTO login(LoginRequest loginRequest);
	String refreshAccessToken(RefreshTokenRequest refreshTokenRequest);
	UserDTO me();
	void logout(String refreshToken);
}
 