package com.tonyqis.xiuren.entity;

public class ImageDownEntity {
    private String imageUrl;

    private String path;

    public ImageDownEntity(String imageUrl, String path) {
        this.imageUrl = imageUrl;
        this.path = path;
    }

    private ImageDownEntity() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
