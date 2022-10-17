package ru.drsanches.photobooth.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.app.data.image.dto.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.dto.UploadAvatarDTO;
import ru.drsanches.photobooth.app.data.image.dto.UploadPhotoDTO;
import ru.drsanches.photobooth.app.service.web.ImageWebService;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode200;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode201;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode400;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode401;
import ru.drsanches.photobooth.common.swagger.ApiTokenAuthorization;
import ru.drsanches.photobooth.common.swagger.ApiPaginationPage;
import ru.drsanches.photobooth.common.swagger.ApiPaginationSize;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/image", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageController {

    @Autowired
    private ImageWebService imageWebService;

    @Operation(summary = "Adds new avatar")
    @ApiTokenAuthorization
    @ApiResponseCode201
    @ApiResponseCode400
    @ApiResponseCode401
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/avatar", method = RequestMethod.POST)
    public void uploadAvatar(@RequestBody UploadAvatarDTO uploadAvatarDTO) {
        imageWebService.uploadAvatar(uploadAvatarDTO);
    }

    @Operation(summary = "Returns an image by id")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(path = "/{imageId}/info", method = RequestMethod.GET)
    public ImageInfoDTO getImageInfo(@PathVariable String imageId) {
        return imageWebService.getImageInfo(imageId);
    }

    @Operation(summary = "Returns an image by id")
    @ApiResponseCode200
    @ApiResponseCode400
    @RequestMapping(path = "/{imageId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageId) {
        return imageWebService.getImage(imageId);
    }

    @Operation(summary = "Sends a photo to users")
    @ApiTokenAuthorization
    @ApiResponseCode201
    @ApiResponseCode400
    @ApiResponseCode401
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/photo", method = RequestMethod.POST)
    public void uploadPhoto(@RequestBody UploadPhotoDTO uploadPhotoDTO) {
        imageWebService.uploadPhoto(uploadPhotoDTO);
    }

    @Operation(summary = "Returns all images info that are available to the user")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public List<ImageInfoDTO> getAllInfo(@ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
                                         @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size) {
        return imageWebService.getAllInfo(page, size);
    }

    @Operation(summary = "Returns the last images info that is available to the user")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/last", method = RequestMethod.GET)
    public ImageInfoDTO getLastImageInfo() {
        return imageWebService.getLastImageInfo();
    }

    @Operation(summary = "Removes an avatar for user")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/avatar", method = RequestMethod.DELETE)
    public void deleteAvatar() {
        imageWebService.deleteAvatar();
    }
}
