package me.trihung.auth;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.trihung.dto.TokenDTO;
import me.trihung.entity.User;

@Component
@Slf4j
public class TokenGenerator {
    
    @Autowired
    JwtEncoder accessTokenEncoder;

    @Autowired
    @Qualifier("jwtRefreshTokenEncoder")
    JwtEncoder refreshTokenEncoder;
    
    private String createAccessToken(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject(user.getUsername())
                .build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
    
    public String createAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject(userDetails.getUsername())
                .build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
    
    private String createRefreshToken(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .claim("username", user.getUsername())
                .subject(user.getUsername())
                .build();

        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
    public TokenDTO createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetails user)) {
            throw new BadCredentialsException(
                    MessageFormat.format("principal {0} is not of UserDetail type", authentication.getPrincipal().getClass())
            );
        }

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setUsername(user.getUsername());
        tokenDTO.setAccessToken(createAccessToken(authentication));
        String refreshToken;
        if (authentication.getCredentials() instanceof Jwt jwt) {
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(now, expiresAt);
            long daysUntilExpired = duration.toDays();
            if (daysUntilExpired < 7) {
                refreshToken = createRefreshToken(authentication);
            } else {
                refreshToken = jwt.getTokenValue();
            }
        } else {
            refreshToken = createRefreshToken(authentication);
        }
        tokenDTO.setRefreshToken(refreshToken);
//        System.out.println("refresh new "+refreshToken);
        log.debug("refresh new "+refreshToken);
        return tokenDTO;
    }

}