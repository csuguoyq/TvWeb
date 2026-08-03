package com.qiko.tvweb.base;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <p/>
 * Activity基类
 */
public abstract class RxBaseActivity extends AppCompatActivity {
    protected String LOGTAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Activity", this.getClass().getSimpleName());
        LOGTAG = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        //设置布局内容
        if (getLayoutId() == 0) {
            setViewBinding();
        } else {
            setContentView(getLayoutId());
        }
        //初始化控件
        initViews(savedInstanceState);
        //初始化ToolBar
        initToolBar();
        // 注册返回回调
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 在这里处理返回逻辑
                backPressed();
            }
        });
    }

    /**
     * 设置布局layout
     */
    public int getLayoutId() {
        return 0;
    }

    /**
     * 初始化views
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 初始化toolbar
     */
    public void initToolBar() {

    }

    /**
     * 设置ViewBinding
     */
    public void setViewBinding() {

    }

    public void backPressed() {

    }
}
