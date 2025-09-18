package me.trihung.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import me.trihung.service.CustomUserDetailService;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
	@Autowired
	private JWTtoUserConvertor jwtToUserConverter;
	@Autowired
	private KeyUtils keyUtils;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CustomUserDetailService userDetailsService;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
//	@Autowired
//	private UrlBasedCorsConfigurationSource corsConfigurationSource;
	@Autowired
	private CorsConfigurationSource configurationSource;
	

	private String[] publicApi = { "/api/v1/auth/refresh", "/api/v1/auth/login", "/api/v1/auth/register" };

	@Bean
	@Order(1)
	public SecurityFilterChain publicApiFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher(publicApi).authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.csrf(csrf -> csrf.disable())
		        .cors(cors -> cors.configurationSource(configurationSource))
				.httpBasic(httpBasic -> httpBasic.disable()).oauth2ResourceServer(oauth2 -> oauth2.disable()) // disable
																												// jwt
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain jwtApiFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/api/v1/**")
				.authorizeHttpRequests(auth ->
						auth
						.anyRequest().permitAll())
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(configurationSource))
				.httpBasic(httpBasic -> httpBasic.disable())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtToUserConverter))) // JWT
																														// validation
																														// enabled
																														// here
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(customAuthenticationEntryPoint)
						.accessDeniedHandler(customAccessDeniedHandler));

		return http.build();
	}

	@Bean
	@Primary
	JwtDecoder jwtAccessTokenDecoder() {
		return NimbusJwtDecoder.withPublicKey(keyUtils.getAccessTokenPublicKey()).build();
	}

	@Bean
	@Primary
	JwtEncoder jwtAccessTokenEncoder() {
		JWK jwk = new RSAKey.Builder(keyUtils.getAccessTokenPublicKey()).privateKey(keyUtils.getAccessTokenPrivateKey())
				.build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	@Bean
	@Qualifier("jwtRefreshTokenDecoder")
	JwtDecoder jwtRefreshTokenDecoder() {
		return NimbusJwtDecoder.withPublicKey(keyUtils.getRefreshTokenPublicKey()).build();
	}

	@Bean
	@Qualifier("jwtRefreshTokenEncoder")
	JwtEncoder jwtRefreshTokenEncoder() {
		JWK jwk = new RSAKey.Builder(keyUtils.getRefreshTokenPublicKey())
				.privateKey(keyUtils.getRefreshTokenPrivateKey()).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	@Bean
	@Qualifier("jwtRefreshTokenAuthProvider")
	JwtAuthenticationProvider jwtRefreshTokenAuthProvider() {
		JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
		provider.setJwtAuthenticationConverter(jwtToUserConverter);
		return provider;
	}

	// Manager authen tổng hợp các authen provider
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.authenticationProvider(daoAuthenticationProvider()) // Gắn Dao Authen provider vào

				.build();
	}

	// register 1 provider
	//Dao authen provider để register provider giúp lấy thông tin user từ token jwt 
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}
}