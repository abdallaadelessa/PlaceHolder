package com.abdallaadelessa.android.dataplaceholder;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;


/**
 * Created by abdalla on 29/07/15.
 */
public class DataPlaceHolder extends FrameLayout {
    private static final String TAG_PLACE_HOLDER = "PlaceHolder";
    private View vgParentLayout;
    private ImageView ivState;
    private TextView tvMessage;
    private Button btnAction;
    private ProgressWheel pbProgress;
    //-->
    private int mDimModeColor;
    private boolean mHideContent;

    public DataPlaceHolder(Context context) {
        super(context);
        initUI(context);
    }

    public DataPlaceHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
        readAttributeSet(context, attrs, -1);
    }

    public DataPlaceHolder(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initUI(context);
        readAttributeSet(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        hideContent(mHideContent);
    }

    private static void logError(Exception e) {
        Log.e(TAG_PLACE_HOLDER, "PlaceHolder Error", e);
    }

    // ------------------------->

    private void initUI(Context cxt) {
        try {
            View view = inflate(cxt, R.layout.view_dataplaceholder, this);
            vgParentLayout = view.findViewById(R.id.vgParentLayout);
            ivState = (ImageView) view.findViewById(R.id.imgState);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            btnAction = (Button) view.findViewById(R.id.btnRunnable);
            pbProgress = (ProgressWheel) view.findViewById(R.id.pbProgress);
            setDimColor(ContextCompat.getColor(cxt, R.color.colorDim));
            dismissAll();
        } catch (Exception e) {
            logError(e);
        }
    }

    private void readAttributeSet(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a;
        if (defStyle != -1) {
            a = context.obtainStyledAttributes(attrs, R.styleable.app, defStyle, 0);
        } else {
            a = context.obtainStyledAttributes(attrs, R.styleable.app);
        }
        int dimModeColor = a.getColor(R.styleable.app_dimModeColor, -1);
        int messageTextColor = a.getColor(R.styleable.app_messageTextColor, -1);
        int progressBarColor = a.getColor(R.styleable.app_progressBarColor, -1);
        int actionButtonBgColor = a.getColor(R.styleable.app_actionButtonBgColor, -1);
        int actionButtonTextColor = a.getColor(R.styleable.app_actionButtonTextColor, -1);
        String messageText = a.getString(R.styleable.app_showMessage);
        int progress = a.getInt(R.styleable.app_showProgress, -1);
        int dimProgress = a.getInt(R.styleable.app_showDimProgress, -1);
        int stateImageResId = a.getResourceId(R.styleable.app_showStateImage, -1);
        int stateImageWidth = (int) a.getDimension(R.styleable.app_stateImageWidth, -1);
        int stateImageHeight = (int) a.getDimension(R.styleable.app_stateImageHeight, -1);
        int progressSize = (int) a.getDimension(R.styleable.app_progressSize, -1);
        // Set Props
        if (dimModeColor != -1) setDimColor(dimModeColor);
        if (messageTextColor != -1) setMessageTextColor(messageTextColor);
        if (progressBarColor != -1) setProgressWheelColor(progressBarColor);
        if (actionButtonBgColor != -1) setActionButtonBackgroundColor(actionButtonBgColor);
        if (actionButtonTextColor != -1) setActionButtonTextColor(actionButtonTextColor);
        if (stateImageWidth != -1) getStateImageView().getLayoutParams().width = stateImageWidth;
        if (stateImageHeight != -1) getStateImageView().getLayoutParams().height = stateImageHeight;
        if (progressSize != -1) getProgressWheel().setCircleRadius(progressSize);
        showMessage(messageText, progress, stateImageResId);
        if (dimProgress != -1) showDimProgress(dimProgress);
        // Recycle
        a.recycle();
    }

    private void dimParentBackground() {
        vgParentLayout.setClickable(true);
        vgParentLayout.setBackgroundColor(mDimModeColor);
    }

    private void clearParentLayoutBackground() {
        vgParentLayout.setClickable(false);
        vgParentLayout.setBackgroundDrawable(null);
    }

    private void hideContent(boolean hide) {
        mHideContent = hide;
        for (int i = 1; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(hide ? GONE : VISIBLE);
        }
    }

    private void hideDataPlaceHolderViews() {
        clearParentLayoutBackground();
        btnAction.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
        pbProgress.setVisibility(View.GONE);
        ivState.setVisibility(View.GONE);
    }

    // -------------------------> Actions

    private void dismissAllWithContent() {
        hideContent(true);
        hideDataPlaceHolderViews();
    }

    public void dismissAll() {
        hideContent(false);
        hideDataPlaceHolderViews();
    }

    public void showMessage(String message, int progress, int stateImageResId, final int actionTextResId, final Runnable action) {
        if (message != null || progress != -1 || stateImageResId != -1 || actionTextResId != -1 || action != null) {
            dismissAllWithContent();
            if (progress != -1) {
                pbProgress.setVisibility(VISIBLE);
                if (progress == 0) {
                    //Indeterminate
                    pbProgress.spin();
                } else {
                    //Determinate
                    float instantProgress = ((float) progress) / 100.0f;
                    pbProgress.setInstantProgress(instantProgress);
                }
            }
            if (message != null) {
                tvMessage.setText(message);
                tvMessage.setVisibility(View.VISIBLE);
            }
            if (action != null) {
                btnAction.setVisibility(View.VISIBLE);
                if (actionTextResId > 0) {
                    btnAction.setText(actionTextResId);
                }
                btnAction.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        action.run();
                    }
                });
            }
            if (stateImageResId > 0) {
                ivState.setVisibility(View.VISIBLE);
                ivState.setImageResource(stateImageResId);
            }
        }
    }

    public void showMessage(String message, int stateImageResId, final Runnable action) {
        showMessage(message, -1, stateImageResId, -1, action);
    }

    public void showMessage(String message, final Runnable action) {
        showMessage(message, -1, -1, -1, action);
    }

    public void showMessage(String message, int stateImageResId) {
        showMessage(message, -1, stateImageResId, -1, null);
    }

    public void showMessage(String message, int progress, int stateImageResId) {
        showMessage(message, progress, stateImageResId, -1, null);
    }

    public void showMessage(String message) {
        showMessage(message, -1, -1, -1, null);
    }

    public void showStateImage(int stateImageResId, final int actionTextResId, final Runnable action) {
        showMessage(null, -1, stateImageResId, actionTextResId, action);
    }

    public void showStateImage(int stateImageResId, final Runnable action) {
        showMessage(null, -1, stateImageResId, -1, action);
    }

    public void showStateImage(int stateImageResId) {
        showMessage(null, -1, stateImageResId, -1, null);
    }

    public void showActionButton(final int actionTextResId, final Runnable action) {
        showMessage(null, -1, -1, actionTextResId, action);
    }

    public void showActionButton(final Runnable action) {
        showMessage(null, -1, -1, -1, action);
    }

    public void showProgress() {
        showMessage(null, 0, -1, -1, null);
    }

    public void showProgress(int progress) {
        showMessage(null,progress, -1, -1, null);
    }

    public void showDimProgress() {
        showProgress();
        dimParentBackground();
    }

    public void showDimProgress(int progress) {
        showProgress(progress);
        dimParentBackground();
    }

    // -------------------------> Components Getters

    public TextView getMessageTextView() {
        return tvMessage;
    }

    public ImageView getStateImageView() {
        return ivState;
    }

    public Button getActionButton() {
        return btnAction;
    }

    public ProgressWheel getProgressWheel() {
        return pbProgress;
    }

    // -------------------------> Edit Properties

    public void setDataPlaceHolderColor(int color) {
        setProgressWheelColor(color);
        setMessageTextColor(color);
        setActionButtonBackgroundColor(color);
    }

    public void setMessageTextColor(int color) {
        getMessageTextView().setTextColor(color);
    }

    public void setActionButtonBackgroundColor(int color) {
        getActionButton().setBackgroundColor(color);
    }

    public void setActionButtonTextColor(int color) {
        getActionButton().setTextColor(color);
    }

    public void setProgressWheelColor(int color) {
        getProgressWheel().setBarColor(color);
    }

    private void setDimColor(int dimModeColor) {
        mDimModeColor = dimModeColor;
    }

}