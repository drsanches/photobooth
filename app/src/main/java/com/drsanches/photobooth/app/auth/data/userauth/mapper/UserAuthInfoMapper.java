package com.drsanches.photobooth.app.auth.data.userauth.mapper;

import com.drsanches.photobooth.app.auth.data.common.dto.response.UserAuthInfoDTO;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import org.springframework.stereotype.Component;

@Component
public class UserAuthInfoMapper {

    public UserAuthInfoDTO convert(UserAuth userAuth) {
        UserAuthInfoDTO userAuthInfoDTO = new UserAuthInfoDTO();
        userAuthInfoDTO.setId(userAuth.getId());
        userAuthInfoDTO.setUsername(userAuth.getUsername());
        userAuthInfoDTO.setEmail(userAuth.getEmail());
        return userAuthInfoDTO;
    }
}
