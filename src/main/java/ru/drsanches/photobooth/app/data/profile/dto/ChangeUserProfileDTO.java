package ru.drsanches.photobooth.app.data.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangeUserProfileDTO {

    @Schema
    private String firstName;

    @Schema
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "ChangeUserProfileDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}