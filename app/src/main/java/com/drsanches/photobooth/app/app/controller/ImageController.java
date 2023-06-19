package com.drsanches.photobooth.app.app.controller;

import com.drsanches.photobooth.app.app.dto.image.request.UploadAvatarDto;
import com.drsanches.photobooth.app.app.dto.image.request.UploadPhotoDto;
import com.drsanches.photobooth.app.app.dto.image.response.ImageInfoDto;
import com.drsanches.photobooth.app.app.service.ImageWebService;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.swagger.ApiPaginationPage;
import com.drsanches.photobooth.app.common.swagger.ApiPaginationSize;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode201;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/image", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
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
    public void uploadAvatar(@RequestBody UploadAvatarDto uploadAvatarDto) {
        imageWebService.uploadAvatar(uploadAvatarDto);
    }

    @Operation(summary = "Returns an image by id")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(path = "/{imageId}/info", method = RequestMethod.GET)
    public ImageInfoDto getImageInfo(@PathVariable String imageId) {
        return imageWebService.getImageInfo(imageId);
    }

    @Operation(summary = "Returns an image by id")
    @ApiResponseCode200
    @ApiResponseCode400
    @RequestMapping(path = "/{imageId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageId) {
        return imageWebService.getImage(imageId);
    }

    @Operation(summary = "Returns an image thumbnail by id")
    @ApiResponseCode200
    @ApiResponseCode400
    @RequestMapping(path = "{imageId}/thumbnail", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getThumbnail(@PathVariable String imageId) {
        return imageWebService.getThumbnail(imageId);
    }

    @Operation(summary = "Sends a photo to users")
    @ApiTokenAuthorization
    @ApiResponseCode201
    @ApiResponseCode400
    @ApiResponseCode401
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/photo", method = RequestMethod.POST)
    public void uploadPhoto(@RequestBody UploadPhotoDto uploadPhotoDto) {
        imageWebService.uploadPhoto(uploadPhotoDto);
    }

    @Operation(summary = "Returns all images info that are available to the user")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public List<ImageInfoDto> getAllInfo(
            @ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
            @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size
    ) {
        return imageWebService.getAllInfo(page, size);
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
