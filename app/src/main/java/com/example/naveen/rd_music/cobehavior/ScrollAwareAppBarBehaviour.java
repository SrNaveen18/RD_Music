package com.example.naveen.rd_music.cobehavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.example.naveen.rd_music.R;


public class ScrollAwareAppBarBehaviour extends AppBarLayout.ScrollingViewBehavior {

    private Context context;

    private ValueAnimator mAppBarYValueAnimator;
    private boolean mVisible = true;

    private int nestedScrollViewHeight = 0;

    private int appBarHeight = 0;

    private boolean isToolbarhide = false;

    public ScrollAwareAppBarBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof LinearLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        return super.onDependentViewChanged(parent, child, dependency);

        if (nestedScrollViewHeight == 0) {
            setHeight((int) dependency.getY());
        }


        if (appBarHeight == 0) {
            setAppBarHeight(child.getHeight());
        }
        Log.w("App", child.getHeight() + ">>>>>>>>" + dependency.getY());

        Log.w("Nested", child.getHeight() + ">>>>>>>>" + dependency.getY());


        if (nestedScrollViewHeight > dependency.getY()) {
            hideToolbarAnim(child);
            // child.setVisibility(View.GONE);
        } else {
            if (isToolbarhide) {
                showToolbarAnim(child);
            }
            // child.setVisibility(View.VISIBLE);
        }

//        if (child.getHeight() > dependency.getY()) {
//            child.setVisibility(View.GONE);
//            //  setAppBarVisible((AppBarLayout) child,false);
//            // child.setBackgroundColor(Color.BLACK);
//        } else {
//            // setAppBarVisible((AppBarLayout) child,true);
//            child.setVisibility(View.VISIBLE);
//            // child.setBackgroundColor(Color.BLUE);
//        }

        return false;
    }

    private void setHeight(int height) {
        nestedScrollViewHeight = height;
    }


    private void setAppBarHeight(int height) {
        appBarHeight = height;
    }

    private void setStatusBarBackgroundVisible(boolean visible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (visible) {
                Window window = ((Activity) context).getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                Window window = ((Activity) context).getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ContextCompat.getColor(context, android.R.color.transparent));
            }
        }
    }

    private void hideToolbarAnim(View view) {
        view.animate().alpha(1).setDuration(1).translationY(-appBarHeight).setInterpolator(new DecelerateInterpolator());
        isToolbarhide = true;
    }

    private void showToolbarAnim(View view) {
        view.animate().alpha(1).setDuration(1).translationY(0).setInterpolator(new DecelerateInterpolator());
    }


    public void setAppBarVisible(final AppBarLayout appBarLayout, final boolean visible) {

        if (visible == mVisible)
            return;

        if (mAppBarYValueAnimator == null || !mAppBarYValueAnimator.isRunning()) {

            mAppBarYValueAnimator = ValueAnimator.ofFloat((int) appBarLayout.getY(),
                    visible ? (int) appBarLayout.getY() + appBarLayout.getHeight() + getStatusBarHeight() :
                            (int) appBarLayout.getY() - appBarLayout.getHeight() - getStatusBarHeight());

            mAppBarYValueAnimator.setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));

            mAppBarYValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    appBarLayout.setY((Float) animation.getAnimatedValue());

                }
            });

            mAppBarYValueAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (visible)
                        setStatusBarBackgroundVisible(true);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!visible)
                        setStatusBarBackgroundVisible(false);
                    mVisible = visible;
                    super.onAnimationEnd(animation);
                }
            });
            mAppBarYValueAnimator.start();
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
