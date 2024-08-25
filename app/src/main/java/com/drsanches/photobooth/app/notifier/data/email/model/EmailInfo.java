package com.drsanches.photobooth.app.notifier.data.email.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="email_info", indexes = {
        @Index(name = "email_info_user_id_index", columnList = "userId")
})
//TODO: Refactor - move fields from id
public class EmailInfo {

    @EmbeddedId
    private EmailInfoKey id;

    public EmailInfo(String userId, String email) {
        this.id = new EmailInfoKey(userId, email);
    }

    public String getUserId() {
        return id.getUserId();
    }

    public String getEmail() {
        return id.getEmail();
    }
}
