package com.drsanches.photobooth.app.config;

import java.util.function.UnaryOperator;

public class ImageConsts {

    public static final String DEFAULT_AVATAR_ID = "default";

    public static final String DELETED_AVATAR_ID = "deleted";

    public static final String NO_PHOTO_IMAGE_ID = "no_photo";

    public static final String SYSTEM_OWNER_ID = "system";

    public static final UnaryOperator<String> IMAGE_PATH = (String imageId) ->
            "/api/v1/app/image/data/" + imageId;

    public static final UnaryOperator<String> THUMBNAIL_PATH = (String imageId) ->
            "/api/v1/app/image/data/thumbnail/" + imageId;
}
