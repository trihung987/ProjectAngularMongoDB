package me.trihung.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.trihung.entity.User;
import me.trihung.exception.UnauthorizedException;
import me.trihung.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Utility class for security-related operations.
 * Focuses on current user information retrieval from security context.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityHelper {

    private final UserRepository userRepository;

    /**
     * Get the currently authenticated user
     *
     * @return The current user
     * @throws UnauthorizedException if no user is authenticated
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        }else if (principal instanceof User user) {
            username = user.getUsername();
        } else if (principal instanceof Jwt jwt) {
            username = jwt.getSubject();
        } else {
            username = principal.toString();
        }
        // Based on CustomUserDetailService, we're using username as the username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found from "+username));
        return user;
    }

    /**
     * Check if the current user has a specific role
     *
     * @param role The role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().contains("ROLE_" + role));
    }

    /**
     * Check if the current user has any of the specified roles
     *
     * @param roles The roles to check
     * @return true if the user has any of the roles, false otherwise
     */
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the username of the current user
     *
     * @return The username of the current user or empty optional if no user is authenticated
     */
    public Optional<String> getCurrentUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else {
            return Optional.of(principal.toString());
        }
    }

    /**
     * Get the current authentication object from security context
     * 
     * @return The current authentication or null if not authenticated
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
