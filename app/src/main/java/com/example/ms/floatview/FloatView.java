package com.example.ms.floatview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by ms13421 on 2016/10/10.
 */
public class FloatView extends FrameLayout implements View.OnTouchListener {
    private BackRecommendClickListener mBackRecommendClickListener;
    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;

    private boolean mIsRight;//logo是否在右边
    private boolean mIsMove;
    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mWmParams = new WindowManager.LayoutParams();
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置的标准
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 设置悬浮窗的真正停靠位置
        mWmParams.x = mScreenWidth;
        mWmParams.y = mScreenHeight / 3;

        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        addView(createView(context));
        mWindowManager.addView(this, mWmParams);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        if (mIsRight) {
            mWmParams.x = mScreenWidth;
            mWmParams.y = oldY;
        } else {
            mWmParams.x = oldX;
            mWmParams.y = oldY;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    private View createView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 从布局文件获取浮动窗口视图
        View rootFloatView = inflater.inflate(R.layout.view_bus_search_list_back_recommed, null);

        rootFloatView.setOnTouchListener(this);
        rootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackRecommendClickListener.backRecommendOnClick();
            }
        });

        rootFloatView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec
                .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return rootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mIsMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    mIsMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mWmParams.x >= mScreenWidth / 2) {
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    mIsRight = false;
                    mWmParams.x = 0;
                }
                mWindowManager.updateViewLayout(this, mWmParams);
                // 初始化
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return mIsMove;
    }

    public void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void setBackRecommendIcon(int recommendIconId) {
//        ImageView recommendView = (ImageView) findViewById(R.id.recommend_image);
//        Picasso.with(getContext()).load(recommendIconId).into(recommendView);
//    }

    public interface BackRecommendClickListener {
        void backRecommendOnClick();
    }

    public void setRecommendClickListener(BackRecommendClickListener clickListener) {
        mBackRecommendClickListener = clickListener;
    }
}

