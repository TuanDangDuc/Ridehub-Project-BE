package com.tuan.ridehub.service;

import com.tuan.ridehub.model.UserPrincipal;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.loadUserByUsername(username);
        return new UserPrincipal(user);
    }
}
