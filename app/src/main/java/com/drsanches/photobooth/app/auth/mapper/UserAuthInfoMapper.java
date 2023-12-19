package com.drsanches.photobooth.app.auth.mapper;

import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserAuthInfoMapper {

    public UserAuthInfoDto convert(UserAuth userAuth) {
        return UserAuthInfoDto.builder()
                .id(userAuth.getId())
                .username(userAuth.getUsername())
                .email(userAuth.getEmail())
                .passwordExists(StringUtils.isNotEmpty(userAuth.getPassword()))
                .googleAuth(userAuth.getGoogleAuth())
                .build();
    }
}
