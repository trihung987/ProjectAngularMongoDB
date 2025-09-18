package me.trihung.auth;

import java.util.Collections;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JWTtoUserConvertor implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

	@Override
	public UsernamePasswordAuthenticationToken convert(Jwt source) {
		UserDetails user =  new org.springframework.security.core.userdetails.User(
                source.getSubject(),
                "",
                Collections.EMPTY_LIST
        );
		return new UsernamePasswordAuthenticationToken(user, source, Collections.EMPTY_LIST);
	}

}