package ru.drsanches.photobooth.app.data.image.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="image")
public class Image {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    @ToString.Exclude
    private byte[] data;

    @ToString.Include
    private int length() {
        return data.length;
    }
}