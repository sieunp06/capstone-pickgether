package com.capstone.pick.config;

import com.capstone.pick.domain.User;
import com.capstone.pick.dto.UserDto;
import com.capstone.pick.exeption.DuplicatedUserException;
import com.capstone.pick.repository.UserCacheRepository;
import com.capstone.pick.repository.UserRepository;
import com.capstone.pick.security.VotePrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserCacheRepository userCacheRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDto user = null;
        try {
            user = userCacheRepository.getUser(username).orElseGet(() ->
                    userRepository.findById(username)
                            .map(UserDto::from)
                            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        userCacheRepository.setUser(user);
        return VotePrincipal.from(user);
    }

    public void save(UserDto userDto) throws DuplicatedUserException {

        Optional<User> user = userRepository.findById(userDto.getUserId());

        if (user.isEmpty()) { // 동일한 아이디가 없다면 저장
            userRepository.save(userDto.toEntity());
        } else {  // 이미 아이디가 존재하면 예외처리
            throw new DuplicatedUserException();
        }
    }
}
