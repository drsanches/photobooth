package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.auth.data.common.dto.request.GoogleTokenDTO;
import com.drsanches.photobooth.app.auth.service.web.GoogleAuthWebService;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDTO;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/google", produces = MediaType.APPLICATION_JSON_VALUE)
public class GoogleAuthController {

    @Autowired
    private GoogleAuthWebService googleAuthWebService;

    @Operation(summary = "Returns the authorization token and registers a new user account if it doesn't exist")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public TokenDTO getToken(@RequestBody GoogleTokenDTO googleTokenDTO) {
        return googleAuthWebService.getToken(googleTokenDTO);
    }
}
