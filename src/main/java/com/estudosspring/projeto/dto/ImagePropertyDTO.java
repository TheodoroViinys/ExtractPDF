package com.estudosspring.projeto.dto;


public class ImagePropertyDTO {

    private Integer width;
    private Integer heigh;
    private byte[] data;

    public ImagePropertyDTO(Integer width, Integer heigh, byte[] data) {
        this.width = width;
        this.heigh = heigh;
        this.data = data;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeigh() {
        return heigh;
    }

    public void setHeigh(Integer heigh) {
        this.heigh = heigh;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
