package com.example.bookmoth.ui.viewdata;

public class ImageTextItem {
    private String text;
    private boolean isImage;
    private int id;

    public ImageTextItem(String text, boolean isImage, int id) {
        this.text = text;
        this.isImage = isImage;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public boolean isImage() {
        return isImage;
    }

    public int getRid() {
        return id;
    }
}
