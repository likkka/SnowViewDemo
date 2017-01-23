package com.example.rio.snowviewdemo.snowfallview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by rio on 17-1-23.
 */

public class SnowFlake {
    private int size;
    private int alpha;
    private Bitmap bitmap;
    private double speedX;
    private double speedY;
    private double positionX;
    private double positionY;
    private Paint paint;
    private Randomizer randomizer;
    @NotNull
    private SnowFlake.Params params;

    private Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
        }
        return paint;
    }

    private Randomizer getRandomizer() {
        if (randomizer == null) {
            randomizer = new Randomizer();
        }
        return randomizer;
    }

    public void reset(@Nullable Double positionY) {
        size = getRandomizer().randomInt(params.getSizeMinInPx(), params.getSizeMaxInPx(), true);
        if (params.getImage() != null) {
            bitmap = Bitmap.createScaledBitmap(params.getImage(), size, size, false);
        }

        float speed =  (size - params.getSizeMinInPx()) /  (params.getSizeMaxInPx() - params.getSizeMinInPx()) *  (params.getSpeedMax() - params.getSpeedMin()) +  params.getSpeedMin();
        double angle = Math.toRadians(getRandomizer().randomDouble(params.getAngleMax()) * (double) getRandomizer().randomSignum());
        speedX = (double) speed * Math.sin(angle);
        speedY = (double) speed * Math.cos(angle);
        alpha = getRandomizer().randomInt(params.getAlphaMin(), params.getAlphaMax(), false);
        getPaint().setAlpha(alpha);
        positionX = getRandomizer().randomDouble(params.getParentWidth());
        if (positionY != null) {
            this.positionY = positionY.doubleValue();
        } else {
            this.positionY = getRandomizer().randomDouble(params.getParentHeight());
            if (!params.getAlreadyFalling()) {
                this.positionY = this.positionY - (double) params.getParentHeight() - (double) size;
            }
        }

    }

    public void update() {
        positionX += speedX;
        positionY += speedY;
        if (positionY > (double) params.getParentHeight()) {
            reset(Double.valueOf(-((double) size)));
        }

        if (params.getFadingEnabled()) {
            getPaint().setAlpha((int) (alpha * ((params.getParentHeight() - positionY) / params.getParentHeight())));
        }

    }

    public void draw(@NotNull Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, (float)positionX, (float)positionY, getPaint());
        } else {
            canvas.drawCircle((float) positionX,  (float)positionY,  size, getPaint());
        }

    }

    @NotNull
    public SnowFlake.Params getParams() {
        return params;
    }

    public SnowFlake(@NotNull SnowFlake.Params params) {
        super();
        this.params = params;
        alpha = 250;
        paint = getPaint();
        randomizer = getRandomizer();
        reset(null);
    }

    static class Params {
        private int parentWidth;
        private int parentHeight;
        private Bitmap image;
        private int alphaMin;
        private int alphaMax;
        private int angleMax;
        private int sizeMinInPx;
        private int sizeMaxInPx;
        private int speedMin;
        private int speedMax;
        private boolean fadingEnabled;
        private boolean alreadyFalling;

        public Params(int parentWidth, int parentHeight, @Nullable Bitmap image, int alphaMin, int alphaMax, int angleMax, int sizeMinInPx, int sizeMaxInPx, int speedMin, int speedMax, boolean fadingEnabled, boolean alreadyFalling) {
            this.parentWidth = parentWidth;
            this.parentHeight = parentHeight;
            this.image = image;
            this.alphaMin = alphaMin;
            this.alphaMax = alphaMax;
            this.angleMax = angleMax;
            this.sizeMinInPx = sizeMinInPx;
            this.sizeMaxInPx = sizeMaxInPx;
            this.speedMin = speedMin;
            this.speedMax = speedMax;
            this.fadingEnabled = fadingEnabled;
            this.alreadyFalling = alreadyFalling;
        }

        public int getParentWidth() {
            return parentWidth;
        }

        public int getParentHeight() {
            return parentHeight;
        }

        public Bitmap getImage() {
            return image;
        }

        public int getAlphaMin() {
            return alphaMin;
        }

        public int getAlphaMax() {
            return alphaMax;
        }

        public int getAngleMax() {
            return angleMax;
        }

        public int getSizeMinInPx() {
            return sizeMinInPx;
        }

        public int getSizeMaxInPx() {
            return sizeMaxInPx;
        }

        public int getSpeedMin() {
            return speedMin;
        }

        public int getSpeedMax() {
            return speedMax;
        }

        public boolean getFadingEnabled() {
            return fadingEnabled;
        }

        public boolean getAlreadyFalling() {
            return alreadyFalling;
        }


        @NotNull
        public SnowFlake.Params copy(int parentWidth, int parentHeight, @Nullable Bitmap image, int alphaMin, int alphaMax, int angleMax, int sizeMinInPx, int sizeMaxInPx, int speedMin, int speedMax, boolean fadingEnabled, boolean alreadyFalling) {
            return new SnowFlake.Params(parentWidth, parentHeight, image, alphaMin, alphaMax, angleMax, sizeMinInPx, sizeMaxInPx, speedMin, speedMax, fadingEnabled, alreadyFalling);
        }


        public String toString() {
            return "Params(parentWidth=" + parentWidth + ", parentHeight=" + parentHeight + ", image=" + image + ", alphaMin=" + alphaMin + ", alphaMax=" + alphaMax + ", angleMax=" + angleMax + ", sizeMinInPx=" + sizeMinInPx + ", sizeMaxInPx=" + sizeMaxInPx + ", speedMin=" + speedMin + ", speedMax=" + speedMax + ", fadingEnabled=" + fadingEnabled + ", alreadyFalling=" + alreadyFalling + ")";
        }

        public boolean equals(Object var1) {
            if (this != var1) {
                if (var1 instanceof SnowFlake.Params) {
                    SnowFlake.Params var2 = (SnowFlake.Params) var1;
                    if (parentWidth == var2.parentWidth && parentHeight == var2.parentHeight && alphaMin == var2.alphaMin && alphaMax == var2.alphaMax && angleMax == var2.angleMax && sizeMinInPx == var2.sizeMinInPx && sizeMaxInPx == var2.sizeMaxInPx && speedMin == var2.speedMin && speedMax == var2.speedMax && fadingEnabled == var2.fadingEnabled && alreadyFalling == var2.alreadyFalling) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        }
    }
}
