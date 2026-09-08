package com.qiko.tvweb;

import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.qiko.tvweb.base.RxBaseActivity;
import com.qiko.tvweb.databinding.ActivityMainBinding;
import com.qiko.tvweb.utils.ConstantUtil;
import com.qiko.tvweb.utils.ToastUtil;

public class MainActivity extends RxBaseActivity {
    private ActivityMainBinding mBinding;
    private long exitTime;
    private long lastBackHandledTime;
    private long lastKeyEventTime;
    private WebView webView;

    private boolean observerInjected = false;
    private TextView tvClock;
    private Handler clockHandler;
    private Runnable clockRunnable;
    private SimpleDateFormat timeFormat;

    private void injectCloseLoginObserver() {
        if (observerInjected) return;
        String observerJs = "javascript:(function() {" +
                "if (window.__closeLoginObs) return;" +
                "var timer = null;" +
                "var idle = 0;" +
                "function check() {" +
                "timer = null;" +
                "var changed = false; var el;" +
                "el = document.querySelector('.related-video-card-login-guide__footer-close');" +
                "if (el) { try { el.click(); } catch (e) {} changed = true; }" +
                "el = document.querySelector('.YoNA2Hyj.qKr0RhiL');" +
                "if (el) { try { el.click(); } catch (e) {} changed = true; }" +
                "el = document.getElementById('douyin-web-download-guide-container');" +
                "if (el) { try { el.remove(); } catch (e) {} changed = true; }" +
                "idle = changed ? 0 : idle + 1;" +
                "window.__closeLoginDelay = idle >= 5 ? 5000 : 300;" +
                "}" +
                "var obs = new MutationObserver(function() {" +
                "if (!timer) timer = setTimeout(check, window.__closeLoginDelay || 300);" +
                "});" +
                "window.__closeLoginObs = obs;" +
                "obs.observe(document.body, {childList: true, subtree: true});" +
                "check();" +
                "})();";
        webView.evaluateJavascript(observerJs, null);
        observerInjected = true;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            // 先从父容器移除
            webView.removeAllViews();
            webView.stopLoading();

            // 清除缓存和历史记录
            webView.clearHistory();
            webView.clearCache(true);
            webView.clearFormData();

            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        webView = mBinding.webView;
        tvClock = mBinding.tvClock;
        initClock();
        // 隐藏状态栏和导航栏
        getWindow().getDecorView().setSystemUiVisibility(
                webView.SYSTEM_UI_FLAG_FULLSCREEN |
                        webView.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        WebSettings settings = webView.getSettings();
        // 1. 内存优化
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);  // 保留
        // 2. 渲染优化（适合低配置）
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); // 标准布局
        // 3. 减少内存占用
        settings.setLoadsImagesAutomatically(true);  // 保留
        settings.setBlockNetworkImage(false);  // 保留

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null); // 强制硬件加速

        settings.setJavaScriptCanOpenWindowsAutomatically(false);  // 允许JS弹窗
        settings.setAllowFileAccess(true);     // 允许访问本地文件（如需加载本地JS）
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false); // 核心设置
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕宽度
        settings.setUseWideViewPort(true);      // 启用视口适配
        settings.setSupportZoom(false);         // 禁用缩放
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true); // 允许第三方Cookie[10]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setDatabaseEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                // 拦截非HTTP/HTTPS协议
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    try {
                        return true;
                    } catch (ActivityNotFoundException e) {
                        return true;
                    }
                }
                // 拦截抖音强制跳转客户端的链接（如 douyin://）
                if (url.startsWith("douyin://") || url.contains("snssdk1128")) {
                    Toast.makeText(MainActivity.this, "拦截抖音强制跳转客户端的链接", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }



            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String js = "Object.defineProperty(navigator,'platform',{get:function(){return 'Win32';}});" +
                        "Object.defineProperty(navigator,'webdriver',{get:function(){return false;}});" +
                        "Object.defineProperty(navigator,'vendor',{get:function(){return 'Google Inc.';}});" +
                        //"Object.defineProperty(navigator,'deviceMemory',{get:function(){return 8;}});" +
                        //"Object.defineProperty(navigator,'hardwareConcurrency',{get:function(){return 8;}});" +
                        //"Object.defineProperty(navigator,'languages',{get:function(){return['zh-CN','zh','en'];}});" +
                        // "Object.defineProperty(navigator,'plugins',{get:function(){return[0,1,2,3,4,5];}});" +
                        "if(!window.chrome){window.chrome={runtime:{},loadTimes:function(){},csi:function(){},app:{}}};";
                view.evaluateJavascript(js, null);
            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                return null;
//            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.e("WebViewError", request.getMethod() + " " + request.getUrl() + " -> " + errorResponse.getStatusCode());
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("WebViewError", request.getUrl() + " errorCode=" + error.getErrorCode() + " " + error.getDescription());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // 拦截抖音扫码登录轮询接口,伪装成二维码已过期,让SDK停止轮询避免卡顿
                if (url != null && url.contains("/passport/web/check_qrconnect")) {
                    String expiredJson = "{\"data\":{\"status\":\"new\"},\"message\":\"success\"}";
                    return new WebResourceResponse("application/json", "utf-8", 200, "OK", null, new ByteArrayInputStream(expiredJson.getBytes(StandardCharsets.UTF_8)));
                }
                return null;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String jsCode = "document.querySelector('video')?.muted = false;";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(jsCode, null);
                }
                webView.postDelayed(() -> injectCloseLoginObserver(), 2000);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                //Log.d("WebViewConsole", consoleMessage.message() + " -- line " + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // 如果是验证码相关提示，直接确认（但不显示窗口）
                if (message != null && message.contains("验证")) {
                    result.confirm(); // 告诉网页已确认
                    return true;     // 拦截，不显示弹窗
                }
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                if (message != null && message.contains("验证")) {
                    result.confirm();
                    return true;
                }
                return super.onJsConfirm(view, url, message, result);
            }
        });
        // 加载抖音推荐页
        webView.loadUrl("https://www.douyin.com/?recommend=1&from_nav=1");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != KeyEvent.KEYCODE_BACK && (event.getAction() != KeyEvent.ACTION_UP || System.currentTimeMillis() - lastKeyEventTime < 800)) return true;
        lastKeyEventTime = System.currentTimeMillis();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                webView.evaluateJavascript(
                        ConstantUtil.JSCODE_TYPE.KEYCODE_DPAD_LEFT, null);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                webView.evaluateJavascript(
                        ConstantUtil.JSCODE_TYPE.KEYCODE_DPAD_RIGHT, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                // value 就是 JS 代码执行后的返回值
                                String ret = value;
                                //mBinding.tvDebug.setVisibility(View.VISIBLE);
                                //mBinding.tvDebug.setText(ret);
                                // 注意：
                                // 1. value 可能是 JSON 格式的字符串
                                // 2. 如果没有返回值，value 可能为 null 或 "null"
                                // 3. 如果 JS 执行出错，可能不会有回调
                            }
                        });
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                webView.evaluateJavascript(
                        ConstantUtil.JSCODE_TYPE.KEYCODE_DPAD_UP, null);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                webView.evaluateJavascript(
                        ConstantUtil.JSCODE_TYPE.KEYCODE_DPAD_DOWN, null);
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                //if (webView.canGoForward()) {
                //    webView.goForward(); // 优先WebView内部返回[6]
                //} else {
                webView.evaluateJavascript(
                        ConstantUtil.JSCODE_TYPE.KEYCODE_DPAD_CENTER, null);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                webView.evaluateJavascript(
                        ConstantUtil.JSCODE_TYPE.KEYCODE_VOLUME_UP, null);
                return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setViewBinding() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    public void onBack() {
        mBinding.tvDebug.setVisibility(View.GONE);
        webView.evaluateJavascript(
                ConstantUtil.JSCODE_TYPE.KEYCODE_BACK, null);
        if (webView.canGoBack()) {
            webView.goBack(); // 优先WebView内部返回[6]
        }
    }

    @Override
    public void backPressed() {
        onBack();
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtil.longShow("再按一次退出应用");
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private void initClock() {
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        clockHandler = new Handler(Looper.getMainLooper());
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                if (tvClock != null) {
                    tvClock.setText(timeFormat.format(new Date()));
                }
                // Schedule next update at the start of next minute
                long now = System.currentTimeMillis();
                long delay = TimeUnit.MINUTES.toMillis(1) - (now % TimeUnit.MINUTES.toMillis(1));
                clockHandler.postDelayed(this, delay);
            }
        };
        // Start immediately
        clockHandler.post(clockRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.post(clockRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
    }
}