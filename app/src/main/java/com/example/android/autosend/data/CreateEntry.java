package com.example.android.autosend.data;

/**
 * Created by Ishmita on 28-12-2016.
 */
public class CreateEntry {
    private String heading;
    private int image;
    private String actionButton;

    public String getActionButton() {
        return actionButton;
    }

    public void setActionButton(String actionButton) {
        this.actionButton = actionButton;
    }

    public String getHeading() {
        return heading;
    }

    public int getImage() {
        return image;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
