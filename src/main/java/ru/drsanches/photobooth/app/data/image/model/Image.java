package ru.drsanches.photobooth.app.data.image.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="image")
public class Image {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    private byte[] data;

    public Image() {}

    public Image(String id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", length='" + data.length + '\'' +
                '}';
    }
}