package gallery;

import android.view.View;

import java.io.Serializable;

/**
 * Created by ！ on 2016/9/2.
 */
public class SimpleViewTarget implements Serializable {
    private int locationX;
    private int locationY;
    private int width;
    private int height;

    public SimpleViewTarget(int locationX, int locationY, int width, int height) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.width = width;
        this.height = height;
    }

    public SimpleViewTarget(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        this.locationX = location[0];
        this.locationY = location[1];
        this.width = v.getWidth();
        this.height = v.getHeight();
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}