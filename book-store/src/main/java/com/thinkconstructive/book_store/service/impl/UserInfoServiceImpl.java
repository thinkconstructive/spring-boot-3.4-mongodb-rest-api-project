package com.thinkconstructive.book_store.service.impl;

import com.thinkconstructive.book_store.dto.UserInfoDto;
import com.thinkconstructive.book_store.entity.UserInfo;
import com.thinkconstructive.book_store.mapper.UserInfoMapper;
import com.thinkconstructive.book_store.repository.UserInfoRepository;
import com.thinkconstructive.book_store.service.JWTService;
import com.thinkconstructive.book_store.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    public JWTService jwtService;

    @Override
    public UserInfoDto createUser(UserInfoDto userInfoDto) {
        UserInfo userInfo = UserInfoMapper.toEntity(userInfoDto);
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userInfoRepository.save(userInfo);
        return UserInfoMapper.toDto(userInfo);
    }

    @Override
    public String getUserInfo(UserInfoDto userInfoDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userInfoDto.userName(), userInfoDto.password()));
        System.out.println("Authe Onject = " + authentication.isAuthenticated());
        if(authentication.isAuthenticated())
            return jwtService.generateToken(userInfoDto.userName());
        return "Failure";
    }
}
