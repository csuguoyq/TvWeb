package com.qiko.tvweb.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 常量工具类
 */

public class ConstantUtil {

    public class JSCODE_TYPE {
        public static final String KEYCODE_DPAD_UP = "javascript:(function() { " +
                "document.getElementsByClassName(\"xgplayer-playswitch-prev\")[0]?.click();" +
                "})();";
        public static final String KEYCODE_DPAD_DOWN = "javascript:(function() { " +
                "document.getElementsByClassName(\"xgplayer-playswitch-next\")[0]?.click();" +
                "})();";
        public static final String KEYCODE_DPAD_LEFT = "javascript:(function() { " +
                "var switchBtn = document.getElementsByClassName('xg-switch')[1];if(switchBtn){var isChecked = switchBtn.classList.contains('xg-switch-checked');if(!isChecked){ switchBtn?.click();}}" +
                "document.querySelector('#videoSideCard')?.parentElement?.querySelector('[data-e2e=\"video-avatar\"]')?.click();" +
                "})();";
        //调试用
//        public static final String KEYCODE_DPAD_RIGHT = "javascript:(function() { " +
//                "document.querySelector('#videoSideCard')?.parentElement?.querySelector('.xgplayer-page-full-screen')?.children[0]?.click();" +//
//                "var iframe = document.querySelector('iframe[style*=\"display: block\"][src*=\"captcha\"]'); return iframe.closest('div[style*=\"position: fixed\"]').outerHTML;" +
//                "})();";

//        public static final String KEYCODE_DPAD_RIGHT = "javascript:(function() { " +
//                "return JSON.stringify((( {userAgent, platform, language, hardwareConcurrency, deviceMemory, webdriver} ) => ({userAgent, platform, language, hardwareConcurrency, deviceMemory, webdriver}))(window.navigator));" +
//                "})();";
        public static final String KEYCODE_DPAD_RIGHT = "javascript:(function() { " +
                "document.dispatchEvent(new KeyboardEvent('keydown',{key:'k',keyCode:75,code:'KeyK'}));" +
                "})();";
        public static final String KEYCODE_DPAD_CENTER = "javascript:(function() { " +
                "var video = document.querySelector('#videoSideCard')?.parentElement?.querySelector('xg-icon.xgplayer-play')?.children[0]; if (video) {video.click(); return video.outerHTML;} else {var live = document.querySelector('.LivePlayer_Preview:not(.douyin-player-pause) .LiveLinkA'); if (live) {live.click();return live.outerHTML;}}" +
                "})();";
        public static final String KEYCODE_VOLUME_UP = "javascript:(function() { " +
                "document.getElementsByClassName(\"xg-volume-mute\")[1]?.parentElement?.click();" +
                "})();";
        public static final String KEYCODE_BACK = "javascript:(function() { " +
                "document.querySelector('.YoNA2Hyj.qKr0RhiL')?.click();" +
                "document.querySelector('.m9iwbrBe')?.click();" +
                "document.querySelector('.FJuf9mI7')?.parentElement?.click();" +
                "document.querySelector('.vc-captcha-close-btn')?.click();" +
                "document.getElementById('captcha_container')?.remove();" +
                "var dl = document.getElementById('douyin-web-download-guide-container'); if(dl){dl?.remove();}" +
                "document.querySelector('.related-video-card-login-guide__footer-close')?.click();" +
                "document.getElementsByClassName('vc-captcha-close-btn captcha_verify_bar--close in-mobile')[0]?.click();" +
                "document.querySelectorAll('.captcha_verify_bar--close, .captcha_verify-close, .captcha-close-btn, [class*=captcha][class*=close]').forEach(function(el){el.click()});" +
                "document.querySelectorAll('[class*=captcha_verify], [class*=captcha-verify], [id*=captcha], [id*=verify]').forEach(function(el){if(el.offsetParent!==null)el.remove()});" +
                "document.querySelectorAll('div[style*=\"z-index\"], div[class*=\"mask\"], div[class*=\"overlay\"]').forEach(function(el){if(el.offsetParent!==null&&el.innerText.includes('验证'))el.remove()});" +
                "Array.from(document.querySelectorAll('button,div,span,i,a')).find(function(el){return el.textContent.includes('取消')||el.textContent.includes('关闭')||el.textContent.includes('不验证')})?.click();" +
                "document.evaluate('//div[text()=\"退出直播间\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null)?.singleNodeValue?.nextElementSibling?.click();" +
                "document.evaluate('//div[text()=\"登录后免费畅享高清视频\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null)?.singleNodeValue?.nextElementSibling?.click();" +
                "Array.from(document.querySelectorAll('button')).find(button =>button.textContent.includes('继续观看') || button.textContent.includes('我知道了') || button.textContent.includes('确定'))?.click();" +
                "})();";
        public static final String CLOSE_LOGIN = "javascript:(function() { " +
                "document.querySelector('.YoNA2Hyj.qKr0RhiL')?.click();" +
                "document.getElementById('captcha_container')?.remove();" +
                "document.querySelector('.related-video-card-login-guide__footer-close')?.click();" +
                "Array.from(document.querySelectorAll('button')).find(button =>button.textContent.includes('继续观看') || button.textContent.includes('我知道了') || button.textContent.includes('确定'))?.click();" +
                "document.evaluate('//div[text()=\"登录后免费畅享高清视频\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null)?.singleNodeValue?.nextElementSibling?.click();"+
                "})();";
    }
}
