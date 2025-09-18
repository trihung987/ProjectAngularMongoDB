package me.trihung.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import me.trihung.entity.User;
import me.trihung.repository.UserRepository;

@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    //Setup hàm tìm user cho DaoProvider ở security config giúp tìm được user trong jwt khi valiadate
    //Đồng thời cập nhập role simpleAuthority từ table role trong entity user
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	log.debug("loadUserByUsername called with: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        MessageFormat.format("User với username {0} không tìm thấy", username)
                ));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) 
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

}
