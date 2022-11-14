package ru.drsanches.photobooth.auth.data.userauth.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.auth.data.common.dto.response.UserAuthInfoDTO;
import ru.drsanches.photobooth.auth.data.userauth.model.UserAuth;

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
