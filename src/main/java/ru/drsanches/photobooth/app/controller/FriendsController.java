package ru.drsanches.photobooth.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.app.data.friends.dto.request.RemoveRequestDTO;
import ru.drsanches.photobooth.app.data.friends.dto.request.SendRequestDTO;
import ru.drsanches.photobooth.app.data.profile.dto.response.UserInfoDTO;
import ru.drsanches.photobooth.app.service.web.FriendsWebService;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode200;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode201;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode400;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode401;
import ru.drsanches.photobooth.common.swagger.ApiTokenAuthorization;
import ru.drsanches.photobooth.common.swagger.ApiPaginationPage;
import ru.drsanches.photobooth.common.swagger.ApiPaginationSize;

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
    public List<UserInfoDTO> getFriends(@ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
                                        @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size) {
        return friendsWebService.getFriends(page, size);
    }

    @Operation(summary = "Returns a list of information about users from whom a friend request was received")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    public List<UserInfoDTO> getIncomingRequests(@ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
                                                 @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size) {
        return friendsWebService.getIncomingRequests(page, size);
    }

    @Operation(summary = "Returns a list of information about users to whom a friend request was sent")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    public List<UserInfoDTO> getOutgoingRequests(@ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
                                                 @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size) {
        return friendsWebService.getOutgoingRequests(page, size);
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
