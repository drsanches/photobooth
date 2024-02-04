package com.drsanches.photobooth.app.common.notifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationParams {

    private String email;

    private String userId;

    private String code;

    private String account;

    private String fromUser;

    private List<String> toUsers;

    private String imageId;
}
