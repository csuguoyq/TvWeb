# TvWeb

基于 Android WebView 的电视端抖音 Web 版客户端。在 TV / 盒子等大屏设备上以横屏全屏加载抖音推荐页，支持通过遥控器按键操控视频播放，并针对电视场景做了大量适配优化。

## 功能特性

- **全屏横屏沉浸式浏览**：强制横屏，隐藏状态栏与导航栏，模拟 Windows Chrome 桌面环境访问 `https://www.douyin.com`。
- **遥控器按键映射**：通过 JS 注入将 DPAD 方向键、确认键、音量键等映射为视频控制操作。
  - `DPAD_LEFT`：进入主页，返回推荐页
  - `DPAD_RIGHT`：切换主页连续播放方式
  - `DPAD_UP` / `DPAD_DOWN`：上一个 / 下一个视频（倍速切换）
  - `DPAD_CENTER` / `ENTER`：播放 / 暂停，直播场景进入直播间
  - `VOLUME_UP`：切换静音
  - `BACK`：返回上一页，同时自动关闭各种弹窗（登录引导、验证码、下载引导等）
- **弹窗自动清理**：注入 MutationObserver 监听页面，自动关闭登录引导、下载引导等干扰弹窗。
- **请求拦截**：
  - 拦截抖音强制跳转客户端的链接（`douyin://` 等）。
  - 拦截扫码登录轮询接口，伪装为过期以停止轮询、避免卡顿。
  - 自动确认验证码相关 JS 弹窗。
- **WebView 适配**：自动播放（`mediaPlaybackRequiresUserGesture=false`）、混合内容允许、第三方 Cookie、硬件加速等设置，保证视频流畅播放。

## 环境要求

- JDK 11+
- Android SDK（compileSdk 36，minSdk 24）
- 需要有效的签名文件 `qiko_key.jks`（位于项目根目录）

## 构建

```bash
# 在项目根目录执行
./gradlew assembleDebug    # 构建 Debug 包
./gradlew assembleRelease  # 构建 Release 包
```

Debug 与 Release 均使用 `qiko_key.jks` 签名。Release 包已开启混淆（minifyEnabled），输出 APK 文件名格式为 `Qiko_WebTV_v{versionName}{MMddHHmm}.apk`。

## 项目结构

```
app/src/main/java/com/qiko/tvweb/
├── MainActivity.java          # 主界面：WebView 配置、遥控器按键映射、请求拦截
├── QikoApp.java               # Application 入口
├── base/RxBaseActivity.java   # Activity 基类（ViewBinding、返回回调）
└── utils/
    ├── ConstantUtil.java      # 常量与遥控器按键对应的 JS 代码
    └── ToastUtil.java         # Toast 工具
```

## 版本

- 当前版本：1.0.2（versionCode 2）
