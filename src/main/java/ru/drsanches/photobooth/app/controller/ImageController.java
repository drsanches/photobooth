package ru.drsanches.photobooth.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.app.data.image.dto.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.dto.UploadAvatarDTO;
import ru.drsanches.photobooth.app.data.image.dto.UploadPhotoDTO;
import ru.drsanches.photobooth.app.service.web.ImageWebService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/image")
public class ImageController {

    public static final String IMAGE_PATH_PREFIX = "/api/v1/image/";

    @Autowired
    private ImageWebService imageWebService;

    @RequestMapping(path = "/avatar", method = RequestMethod.POST)
    @Operation(summary = "Adds new avatar")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadAvatar(@RequestBody UploadAvatarDTO uploadAvatarDTO) {
        imageWebService.uploadAvatar(uploadAvatarDTO);
    }

    @RequestMapping(path = "/{imageId}/info", method = RequestMethod.GET)
    @Operation(summary = "Returns an image by id")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public ImageInfoDTO getImageInfo(@PathVariable String imageId) {
        return imageWebService.getImageInfo(imageId);
    }

    @RequestMapping(path = "/{imageId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "Returns an image by id")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public byte[] getImage(@PathVariable String imageId) {
        return imageWebService.getImage(imageId);
    }

    @RequestMapping(path = "/photo", method = RequestMethod.POST)
    @Operation(summary = "Sends a photo to users")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadPhoto(@RequestBody UploadPhotoDTO uploadPhotoDTO) {
        imageWebService.uploadPhoto(uploadPhotoDTO);
    }

    @RequestMapping(path = "/all", method = RequestMethod.GET)
    @Operation(summary = "Returns all images info that are available to the user")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public List<ImageInfoDTO> getAllInfo() {
        return imageWebService.getAllInfo();
    }

    @RequestMapping(path = "/last", method = RequestMethod.GET)
    @Operation(summary = "Returns the last images info that is available to the user")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public ImageInfoDTO getLastImageInfo() {
        return imageWebService.getLastImageInfo();
    }

    @RequestMapping(path = "/avatar", method = RequestMethod.DELETE)
    @Operation(summary = "Removes an avatar for user")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void deleteAvatar() {
        imageWebService.deleteAvatar();
    }
}
