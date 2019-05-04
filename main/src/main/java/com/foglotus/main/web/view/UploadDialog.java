package com.foglotus.main.web.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.graphics.ColorUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.foglotus.main.R;

/**
 * @author foglotus
 * @since 2019/4/16
 */
public class UploadDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private View mDialogView;
    private EditText mUploadPath;
    private EditText mUploadName;
    private ProgressBar mProgress;
    private TextView mProgressText;
    private TextView mSize;
    private TextView mError;
    private Button mFolderSelect;
    private Button mConfirmButton;
    private Button mCancelButton;
    private UploadDialog.OnClickListener mConfirmClickListener;
    private UploadDialog.OnClickListener mCancelClickListener;
    private UploadDialog.OnClickListener mFolderClickListener;

    public UploadDialog(Context context) {
        super(context, R.style.alert_dialog);
        this.activity = (Activity) context;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_web_upload_add);
        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);

        mUploadPath = findViewById(R.id.uploadPath);
        mUploadName = findViewById(R.id.uploadName);
        mProgress = findViewById(R.id.progress);
        mProgressText = findViewById(R.id.progressText);
        mSize = findViewById(R.id.size);
        mError = findViewById(R.id.error);

        mFolderSelect = findViewById(R.id.folderSelect);
        mConfirmButton = findViewById(R.id.confirm_button);
        mCancelButton = findViewById(R.id.cancel_button);

        mFolderSelect.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        int id = v.getId();
        if(id == R.id.folderSelect){
            if(mFolderClickListener != null)mFolderClickListener.onClick();
        }else if(id == R.id.cancel_button){
            if(mCancelClickListener != null)mCancelClickListener.onClick();
            super.dismiss();
        }else if(id == R.id.confirm_button){
            if(mConfirmClickListener != null)mConfirmClickListener.onClick();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard(event,new View[]{mUploadName},activity);
        return super.onTouchEvent(event);
    }

    /**
     * 根据传入控件的坐标和用户的焦点坐标，判断是否隐藏键盘，如果点击的位置在控件内，则不隐藏键盘
     *
     * @param views
     *            控件view
     * @param event
     *            焦点位置
     * @return 是否隐藏
     */
    public static void hideKeyboard(MotionEvent event, View[] views,
                                    Activity activity) {
        try {
            for (View view:views){
                if (view != null && view instanceof EditText) {
                    int[] location = { 0, 0 };
                    view.getLocationInWindow(location);
                    int left = location[0], top = location[1], right = left
                            + view.getWidth(), bootom = top + view.getHeight();
                    // 判断焦点位置坐标是否在空间内，如果位置在控件外，则隐藏键盘
                    if (event.getRawX() < left || event.getRawX() > right
                            || event.getY() < top || event.getRawY() > bootom) {
                        // 隐藏键盘
                        IBinder token = view.getWindowToken();
                        InputMethodManager inputMethodManager = (InputMethodManager) activity
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(token,
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UploadDialog showCancelButton (boolean isShow) {
        if (mCancelButton != null) {
            mCancelButton.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public UploadDialog showConfirmButton (boolean isShow) {
        if (mConfirmButton != null) {
            mConfirmButton.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
        return this;
    }


    public interface OnClickListener{
        void onClick();
    }

    public void setConfirmClickListener(OnClickListener mConfirmClickListener) {
        this.mConfirmClickListener = mConfirmClickListener;
    }

    public void setCancelClickListener(OnClickListener mCancelClickListener) {
        this.mCancelClickListener = mCancelClickListener;
    }

    public void setFolderClickListener(OnClickListener mFolderClickListener) {
        this.mFolderClickListener = mFolderClickListener;
    }

    public String getUploadPath() {
        return mUploadPath.getText().toString();
    }

    public void setUploadPath(String uploadPath) {
        this.mUploadPath.setText(uploadPath);
        setUploadName(uploadPath.substring(uploadPath.lastIndexOf("/")+1));
    }

    public String getUploadName() {
        return mUploadName.getText().toString();
    }

    private void setUploadName(String uploadName) {
        this.mUploadName.setText(uploadName);
    }


    public void setProgress(int progress) {
        this.mProgress.setProgress(progress);
        setProgressText(progress+"%");
    }

    public void setSize(String size) {
        this.mSize.setText(size);
    }

    private void setProgressText(String progress) {
        this.mProgressText.setText(progress);
    }


    public void setError(String error, boolean isError) {
        this.mError.setText(error);
        if(isError){
            mError.setTextColor(activity.getColor(R.color.error));
        }else{
             mError.setTextColor(activity.getColor(R.color.success));
        }
    }
}
