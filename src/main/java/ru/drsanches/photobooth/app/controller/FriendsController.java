package ru.drsanches.photobooth.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.app.data.friends.dto.RemoveRequestDTO;
import ru.drsanches.photobooth.app.data.friends.dto.SendRequestDTO;
import ru.drsanches.photobooth.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.photobooth.app.service.web.FriendsWebService;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/friends")
public class FriendsController {

    @Autowired
    private FriendsWebService friendsWebService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    @Operation(summary = "Returns a list of friends information")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public List<UserInfoDTO> getFriends() {
        return friendsWebService.getFriends();
    }

    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    @Operation(summary = "Returns a list of information about users from whom a friend request was received")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public List<UserInfoDTO> getIncomingRequests() {
        return friendsWebService.getIncomingRequests();
    }

    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    @Operation(summary = "Returns a list of information about users to whom a friend request was sent")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public List<UserInfoDTO> getOutgoingRequests() {
        return friendsWebService.getOutgoingRequests();
    }

    @RequestMapping(path = "/manage/add", method = RequestMethod.POST)
    @Operation(summary = "Sends a friend request or confirms of another user's request")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(@RequestBody SendRequestDTO sendRequestDTO) {
        friendsWebService.sendRequest(sendRequestDTO);
    }

    @RequestMapping(path = "/manage/delete", method = RequestMethod.POST)
    @Operation(summary = "Cancels the friend request from the current or to another user or removes user from friends")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void removeRequest(@RequestBody RemoveRequestDTO removeRequestDTO) {
        friendsWebService.removeRequest(removeRequestDTO);
    }
}