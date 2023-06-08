package com.drsanches.photobooth.app.auth.data.userauth.mapper;

import com.drsanches.photobooth.app.auth.data.common.dto.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import org.springframework.stereotype.Component;

@Component
public class UserAuthInfoMapper {

    public UserAuthInfoDto convert(UserAuth userAuth) {
        UserAuthInfoDto userAuthInfoDto = new UserAuthInfoDto();
        userAuthInfoDto.setId(userAuth.getId());
        userAuthInfoDto.setUsername(userAuth.getUsername());
        userAuthInfoDto.setEmail(userAuth.getEmail());
        return userAuthInfoDto;
    }
}
