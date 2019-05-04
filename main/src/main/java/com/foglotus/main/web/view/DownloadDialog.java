package com.foglotus.main.web.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.foglotus.main.R;

/**
 * @author foglotus
 * @since 2019/4/16
 */
public class DownloadDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private View mDialogView;
    private EditText mDownloadUrl;
    private EditText mDownloadName;
    private EditText mDownloadPath;
    private Button mFolderSelect;
    private Button mConfirmButton;
    private Button mCancelButton;
    private DownloadDialog.OnClickListener mConfirmClickListener;
    private DownloadDialog.OnClickListener mCancelClickListener;
    private DownloadDialog.OnClickListener mFolderClickListener;

    public DownloadDialog(Context context) {
        super(context, R.style.alert_dialog);
        this.activity = (Activity) context;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_web_download_add);
        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mDownloadUrl = (EditText) findViewById(R.id.downloadUrl);
        mDownloadName = findViewById(R.id.downloadName);
        mDownloadPath = findViewById(R.id.downloadPath);
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
        hideKeyboard(event,new View[]{mDownloadPath,mDownloadName,mDownloadPath},activity);
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

    public void setDownloadUrl(String url){
        if(mDownloadUrl != null && url != null)
            mDownloadUrl.setText(url);
    }

    public void setDownloadName(String name){
        if(mDownloadName != null && name!=null)
            mDownloadName.setText(name);
    }
    public void setDownloadPath(String path){
        if(mDownloadPath != null && path!=null)
            mDownloadPath.setText(path);
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

    public String getDownloadUrl() {
        return mDownloadUrl.getText().toString();
    }

    public String getDownloadName() {
        return mDownloadName.getText().toString();
    }

    public String getDownloadPath() {
        return mDownloadPath.getText().toString();
    }
}
