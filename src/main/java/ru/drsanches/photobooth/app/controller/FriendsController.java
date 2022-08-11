package ru.drsanches.photobooth.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.app.data.friends.dto.RemoveRequestDTO;
import ru.drsanches.photobooth.app.data.friends.dto.SendRequestDTO;
import ru.drsanches.photobooth.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.photobooth.app.service.web.FriendsWebService;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode200;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode201;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode400;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode401;
import ru.drsanches.photobooth.common.swagger.ApiTokenAuthorization;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/friends", produces = MediaType.APPLICATION_JSON_VALUE)
public class FriendsController {

    @Autowired
    private FriendsWebService friendsWebService;

    @Operation(summary = "Returns a list of friends information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<UserInfoDTO> getFriends() {
        return friendsWebService.getFriends();
    }

    @Operation(summary = "Returns a list of information about users from whom a friend request was received")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    public List<UserInfoDTO> getIncomingRequests() {
        return friendsWebService.getIncomingRequests();
    }

    @Operation(summary = "Returns a list of information about users to whom a friend request was sent")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    public List<UserInfoDTO> getOutgoingRequests() {
        return friendsWebService.getOutgoingRequests();
    }

    @Operation(summary = "Sends a friend request or confirms of another user's request")
    @ApiTokenAuthorization
    @ApiResponseCode201
    @ApiResponseCode400
    @ApiResponseCode401
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/manage/add", method = RequestMethod.POST)
    public void sendRequest(@RequestBody SendRequestDTO sendRequestDTO) {
        friendsWebService.sendRequest(sendRequestDTO);
    }

    @Operation(summary = "Cancels the friend request from the current or to another user or removes user from friends")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(path = "/manage/delete", method = RequestMethod.POST)
    public void removeRequest(@RequestBody RemoveRequestDTO removeRequestDTO) {
        friendsWebService.removeRequest(removeRequestDTO);
    }
}
