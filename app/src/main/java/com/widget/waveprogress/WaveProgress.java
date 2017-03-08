package com.widget.waveprogress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;

import com.example.waveprogress.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/3/7.
 */
public class WaveProgress extends View{

    private Wave wave;
    private Drawable buttonDrawable;
    private int buttonRadius = 0;

    public WaveProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.WaveStyle);
        buttonRadius = array.getDimensionPixelSize(R.styleable.WaveStyle_button_radius,0);
        int circular = array.getResourceId(R.styleable.WaveStyle_button_drawable,R.drawable.circular);
        buttonDrawable = context.getDrawable(circular);
        init();
    }

    public WaveProgress(Context context) {
        super(context);
        init();
    }

    private void init(){
        if(buttonDrawable == null)
            buttonDrawable = getContext().getDrawable(R.drawable.circular);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(wave != null){
                    wave.stop();
                }
                if(buttonRadius == 0)
                    buttonRadius = Math.min(getWidth()/6,getHeight()/6)/2;
                wave = new Wave(getWidth()/2,getHeight()/2,Math.min(getWidth()/2,getHeight()/2),buttonRadius);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(wave != null)
            wave.draw(canvas);
        drawHandler(canvas);
    }

    public void animatorStart(){
        wave.start();
    }

    public void animatorEnd(){
        wave.stop();
    }

    private void drawHandler(Canvas canvas){
        canvas.save();
        buttonDrawable.setBounds(getWidth()/2-buttonRadius,getHeight()/2-buttonRadius,getWidth()/2+buttonRadius,getHeight()/2+buttonRadius);
        buttonDrawable.draw(canvas);
        canvas.restore();
    }

    private class Wave implements AnimatorInterface{

        private List<WaveLayer> layers = new ArrayList<WaveLayer>();

        private WaveLayerListener listener = new WaveLayerListener() {
            @Override
            public void onWaveLayerRepeat(WaveLayer layer) {
                layers.remove(0);
                layers.add(layer);
            }
        };

        public Wave(final int centerX,final int centerY,int maxRadius,final int raduis){
            for(int i = 0;i < 3;i++){
                WaveLayer layer= new WaveLayer(centerX,centerY,maxRadius,raduis,listener);
                layer.setDelay(i*1000);
                layers.add(layer);
            }
        }

        public void draw(Canvas canvas){
            canvas.save();
            for(WaveLayer layer : layers){
                layer.draw(canvas);
            }
            canvas.restore();
        }

        public void start(){
            for(WaveLayer layer : layers){
                layer.start();
            }
        }

        public void stop(){
            for(WaveLayer layer : layers){
                layer.stop();
            }
        }

    }

    private interface AnimatorInterface{
        public void draw(Canvas canvas);
        public void start();
        public void stop();
    }

    private interface WaveLayerListener{
        public void onWaveLayerRepeat(WaveLayer layer);
    }

    private class WaveLayer implements AnimatorInterface {

        private Drawable drawable;
        private ValueAnimator valueAnimator;
        private float scale = 1;
        private float radius;
        private float maxRadius;
        private float centerX;
        private float centerY;
        private WaveLayerListener listener;

        public WaveLayer(int x, int y, float maxRadius, float radius, final WaveLayerListener listener){
            this.listener = listener;
            drawable = getContext().getDrawable(R.drawable.circular_wave);
            this.centerX = x;
            this.centerY = y;
            this.radius = radius;
            this.maxRadius = maxRadius;
            valueAnimator = ValueAnimator.ofFloat(1,maxRadius/radius);
            valueAnimator.setRepeatCount(-1);
            valueAnimator.setRepeatMode(Animation.RESTART);
            valueAnimator.setDuration(3000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (Float)animation.getAnimatedValue();
                    scale = f;
                    postInvalidate();
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animation) {}
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {
                    if(listener != null)
                        listener.onWaveLayerRepeat(WaveLayer.this);
                }
            });
        }

        public void stop(){
            valueAnimator.end();
        }

        private void setDelay(long time){
            valueAnimator.setStartDelay(time);
        }

        public void start(){
            valueAnimator.start();
        }

        public void draw(Canvas canvas) {
            canvas.save();
            float realRadius = radius*scale;
            drawable.setBounds((int)(centerX-realRadius),(int)(centerY-realRadius),(int)(centerX+realRadius),(int)(centerY+realRadius));
            drawable.draw(canvas);
            canvas.restore();
        }
    }
}
