package ru.drsanches.photobooth.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.auth.data.dto.GoogleAccessTokenDTO;
import ru.drsanches.photobooth.auth.data.dto.TokenDTO;
import ru.drsanches.photobooth.auth.service.web.GoogleAuthWebService;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode200;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode201;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode400;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode401;

@RestController
@RequestMapping(value = "/api/v1/auth/google", produces = MediaType.APPLICATION_JSON_VALUE)
public class GoogleAuthController {

    @Autowired
    private GoogleAuthWebService googleAuthWebService;

    @Operation(summary = "Registers new user account and returns user information")
    @ApiResponseCode201
    @ApiResponseCode400
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public TokenDTO registration(@RequestBody GoogleAccessTokenDTO googleAccessTokenDTO) {
        return googleAuthWebService.registration(googleAccessTokenDTO);
    }

    @Operation(summary = "Returns authorization tokens")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public TokenDTO login(@RequestBody GoogleAccessTokenDTO googleAccessTokenDTO) {
        return googleAuthWebService.login(googleAccessTokenDTO);
    }
}
