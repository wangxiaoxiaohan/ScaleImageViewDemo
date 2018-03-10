package example.wanghan.com.scaleimageviewdemo;

import android.content.Context;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import android.support.annotation.Nullable;

import android.util.AttributeSet;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;


/**
 * Created by hs-acer on 2018/3/9.
 */

public class ScaleImageView extends android.support.v7.widget.AppCompatImageView
        implements View.OnTouchListener{
    private int mWidth;//控件宽度
    private  int mHeight;//控件高度
    private Drawable mDrawable;//控件内的Drawable
    private  int  mDrawableWidth;//图片宽度
    private int mDrawableHeight;//图片高度
    private float mScale;//记录初始化图片后缩放倍数。
    private  float mMaxScale;//最大放大倍数
    private  float mMinScale;//最小倍数
    private android.graphics.Matrix mScaleMatrix=new android.graphics.Matrix();
    private  ScaleGestureDetector scaleGestureDetector;
    private ScaleGestureDetector.OnScaleGestureListener  scaleGestureListener=new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale =getScale();//当前已经缩放的缩放比例
            float scaleFactor = detector.getScaleFactor();//通过手势检测检测到的缩放比例
            mMaxScale=2.5f*mScale;
            mMinScale=1.0f*mScale;

            if (getDrawable() == null)
                return true;

            if ((scale <= mMaxScale && scale >= mMinScale && scaleFactor > 1.0f)
                    || (scale >= mMinScale &&  scale <= mMaxScale && scaleFactor < 1.0f))
            {
                /**
                 * 最大值最小值判断
                 */
                if (scaleFactor * scale < mMinScale && scaleFactor<1.0f)
                {
                    scaleFactor = 1.0f;

                }
                if (scaleFactor * scale > mMaxScale && scaleFactor>1.0f)
                {
                    scaleFactor = 1.0f;
                }
                /**
                 * 设置缩放比例
                 */
            }
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());

             RectF f =getRectF(mScaleMatrix);
             // 调整放大缩小后出现的位移差。
             float dx=0.0f;
             float dy=0.0f;
             if (f.height()> mHeight){
                 if (f.top>0){//上方出现空白
                     dy=-f.top;//向上移动
                 }
                 if (f.bottom<mHeight){//下方出现空白
                     dy=mHeight-f.bottom;//向下移动
                 }
             }
             if (f.width()>mWidth){
                 if (f.left>0){//左边出现空白
                     dx=-f.left;//向左移动
                 }
                 if (f.right<mWidth){//右边出现空白
                     dx=mWidth-f.right;//向右移动
                 }
             }

             if (f.width()<mWidth){
                 dx  = mWidth / 2 - f.right + f.width() / 2;
             }
             if (f.height()<mHeight){
                 dy = mHeight / 2 - f.bottom + f.height() / 2;
             }

             mScaleMatrix.postTranslate(dx,dy);



            setImageMatrix(mScaleMatrix);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    };

    private GestureDetector  gestureDetector;
    private GestureDetector.OnGestureListener   gestureListener=new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }
    };




    public ScaleImageView(Context context) {
        this(context,null);

    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOnTouchListener(this);
        scaleGestureDetector=new ScaleGestureDetector(context,scaleGestureListener);
        initListener();
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        this(context,attrs);

    }






    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);

    }


    private   void  initListener(){
        setScaleType(ScaleType.MATRIX);//设置缩放模式为Matrix；scaleType为MATRIX属性的图片都是不经过缩放直接显示在屏幕左上角.
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                mWidth=getWidth();
                mHeight=getHeight();//获取控件宽高
                mDrawable=getDrawable();//获取控件图片
                if (mDrawable==null)
                    return;

                mDrawableWidth=mDrawable.getIntrinsicWidth();
                mDrawableHeight=mDrawable.getIntrinsicHeight();
                initImageViewSize();
                moveToCenter();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);//移除观察者
            }

        });

    }


//这个方法初始化资源图片，初始化缩放倍数
    private void initImageViewSize() {
        if(mDrawable==null){
            return;

        }
        float scale=1.0f;
        if (mDrawableWidth>mWidth && mDrawableHeight <mHeight){
            scale=mWidth*1.0f/mDrawableWidth;
        }
        else  if (mDrawableWidth<mWidth && mDrawableHeight>mHeight){
            scale=mHeight*1.0f/mDrawableHeight;
        }
        else  if (mDrawableWidth>mWidth && mDrawableHeight>mHeight){
            scale=Math.min(mWidth*1.0f/mDrawableWidth,mHeight*1.0f/mDrawableHeight);
        }
        else if(mDrawableWidth<mWidth && mDrawableHeight<mHeight){
            scale=Math.min(mWidth*1.0f/mDrawableWidth,mHeight*1.0f/mDrawableHeight);
        }
        mScale=scale;

    }

    public float getScale() {

        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[4];// 矩阵中第五个value 即为x缩放倍数。  源码开始就有。
    }
    //启用Matrix的图片，默认在ImageView的左上角，因此用此方法将它移动到中心
    private void moveToCenter() {
        final float dx = mWidth / 2 - mDrawableWidth / 2;
        final float dy = mHeight / 2 - mDrawableHeight / 2;

        // 平移至中心
        mScaleMatrix.postTranslate(dx, dy);
        // 以控件中心作为缩放
        mScaleMatrix.postScale(mScale, mScale, mWidth / 2, mHeight / 2);
        setImageMatrix(mScaleMatrix);

    }
    private RectF  getRectF(android.graphics.Matrix  matrix) {
               RectF f =new RectF();
               if (mDrawable==null){
                   return null;
               }
               f.set(0,0,mDrawableWidth,mDrawableHeight);
               matrix.mapRect(f);
               return  f;
    }

}
