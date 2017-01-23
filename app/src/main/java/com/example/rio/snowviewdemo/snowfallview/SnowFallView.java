package com.example.rio.snowviewdemo.snowfallview;

/**
 * Created by rio on 17-1-23.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.rio.snowviewdemo.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;



public class SnowFallView extends View {
    private int DEFAULT_SNOWFLAKES_NUM = 200;
    private int DEFAULT_SNOWFLAKE_ALPHA_MIN = 150;
    private int DEFAULT_SNOWFLAKE_ALPHA_MAX= 250;
    private int DEFAULT_SNOWFLAKE_ANGLE_MAX = 10;
    private int DEFAULT_SNOWFLAKE_SIZE_MIN_IN_DP = 2;
    private int DEFAULT_SNOWFLAKE_SIZE_MAX_IN_DP = 8;
    private int DEFAULT_SNOWFLAKE_SPEED_MIN = 2;
    private int DEFAULT_SNOWFLAKE_SPEED_MAX = 8;
    private boolean DEFAULT_SNOWFLAKES_FADING_ENABLED = false;
    private boolean DEFAULT_SNOWFLAKES_ALREADY_FALLING = false;
    private int snowflakesNum;
    private Bitmap snowflakeImage;
    private int snowflakeAlphaMin;
    private int snowflakeAlphaMax;
    private int snowflakeAngleMax;
    private int snowflakeSizeMinInPx;
    private int snowflakeSizeMaxInPx;
    private int snowflakeSpeedMin;
    private int snowflakeSpeedMax;
    private boolean snowflakesFadingEnabled;
    private boolean snowflakesAlreadyFalling;
    private SnowFallView.UpdateSnowFlakesThread updateSnowFlakesThread;
    private SnowFlake[] snowflakes;

    private  int dpToPx(int dp) {
        return (int)((float)dp * getResources().getDisplayMetrics().density);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.snowflakes = createSnowFlakes();
    }

    protected void onVisibilityChanged(@NotNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(changedView == this && visibility == GONE) {
            if(this.snowflakes != null) {
                for (SnowFlake snowFlake : snowflakes) {
                    snowFlake.reset(null);
                }
            }
        }

    }

    protected void onDraw(@NotNull Canvas canvas) {
        super.onDraw(canvas);
        if(isInEditMode()) {
            return;
        }
        if (snowflakes == null) {
            return;
        }
        for(SnowFlake snowFlake : snowflakes) {
            snowFlake.draw(canvas);
        }
        updateSnowFlakes();
    }

    private SnowFlake[] createSnowFlakes() {
        SnowFlake.Params snowflakeParams = new SnowFlake.Params(this.getWidth(), this.getHeight(), this.snowflakeImage, this.snowflakeAlphaMin, this.snowflakeAlphaMax, this.snowflakeAngleMax, this.snowflakeSizeMinInPx, this.snowflakeSizeMaxInPx, this.snowflakeSpeedMin, this.snowflakeSpeedMax, this.snowflakesFadingEnabled, this.snowflakesAlreadyFalling);
        int size = this.snowflakesNum;
        SnowFlake[] result = new SnowFlake[size];
        for (int i = 0; i < size; i++) {
            result[i] = new SnowFlake(snowflakeParams);
        }
        return result;
    }

    private  void updateSnowFlakes() {
        this.updateSnowFlakesThread.getHandler().post((new Runnable() {
            public  void run() {
                SnowFlake[] snowflakes = SnowFallView.this.snowflakes;
                if(snowflakes != null) {
                    for (SnowFlake snowFlake : snowflakes) {
                        if (snowFlake == null) continue;
                        snowFlake.update();
                    }
                }
                SnowFallView.this.postInvalidateOnAnimation();
            }
        }));
    }

    public SnowFallView(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnowFallView);
        try {
            this.snowflakesNum = a.getInt(R.styleable.SnowFallView_snowflakesNum, DEFAULT_SNOWFLAKES_NUM);
            Drawable snowFlakeDrawable = a.getDrawable(R.styleable.SnowFallView_snowflakeImage);
            this.snowflakeImage = snowFlakeDrawable != null? toBitmap(snowFlakeDrawable):null;
            this.snowflakeAlphaMin = a.getInt(R.styleable.SnowFallView_snowflakeAlphaMin, DEFAULT_SNOWFLAKE_ALPHA_MIN);
            this.snowflakeAlphaMax = a.getInt(R.styleable.SnowFallView_snowflakeAlphaMax, DEFAULT_SNOWFLAKE_ALPHA_MAX);
            this.snowflakeAngleMax = a.getInt(R.styleable.SnowFallView_snowflakeAngleMax, DEFAULT_SNOWFLAKE_ANGLE_MAX);
            this.snowflakeSizeMinInPx = a.getDimensionPixelSize(R.styleable.SnowFallView_snowflakeSizeMin, dpToPx(this.DEFAULT_SNOWFLAKE_SIZE_MIN_IN_DP));
            this.snowflakeSizeMaxInPx = a.getDimensionPixelSize(R.styleable.SnowFallView_snowflakeSizeMax, dpToPx(this.DEFAULT_SNOWFLAKE_SIZE_MAX_IN_DP));
            this.snowflakeSpeedMin = a.getInt(R.styleable.SnowFallView_snowflakeSpeedMin, DEFAULT_SNOWFLAKE_SPEED_MIN);
            this.snowflakeSpeedMax = a.getInt(R.styleable.SnowFallView_snowflakeSpeedMax, DEFAULT_SNOWFLAKE_SPEED_MAX);
            this.snowflakesFadingEnabled = a.getBoolean(R.styleable.SnowFallView_snowflakesFadingEnabled, DEFAULT_SNOWFLAKES_FADING_ENABLED);
            this.snowflakesAlreadyFalling = a.getBoolean(R.styleable.SnowFallView_snowflakesAlreadyFalling, DEFAULT_SNOWFLAKES_ALREADY_FALLING);
        } finally {
            a.recycle();
        }
        updateSnowFlakesThread = new UpdateSnowFlakesThread();
    }

    private Bitmap toBitmap(Drawable snowFlakeDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(snowFlakeDrawable.getIntrinsicWidth(),
                snowFlakeDrawable.getMinimumHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        snowFlakeDrawable.draw(canvas);
        return bitmap;
    }

    private class UpdateSnowFlakesThread extends HandlerThread {
        @NotNull
        public  Handler getHandler() {
            return new Handler(getLooper());
        }
        public UpdateSnowFlakesThread() {
            super("SnowFlakesComputations");
            this.start();
        }
    }
}
