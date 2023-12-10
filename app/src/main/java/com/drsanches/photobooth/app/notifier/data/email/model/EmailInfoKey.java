package com.drsanches.photobooth.app.notifier.data.email.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EmailInfoKey implements Serializable {

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "email", nullable = false)
    private String email;
}
