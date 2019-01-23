package de.jnd.hki.model;

public class Number {
    private int label;
    private byte[] pixels;

    public Number(int label, byte[] pixels) {
        this.label = label;
        this.pixels = pixels;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public byte[] getPixels() {
        return pixels;
    }

    public void setPixels(byte[] pixels) {
        this.pixels = pixels;
    }
}
