package com.example.bhava.presentationactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.MediaRouteActionProvider;
import android.app.Presentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

public class PresentationWithMediaRouterActivity extends Activity {
    private static final String TAG = "PresentationWithMediaRouterActivity";
    // MediaRouter用于和MediaRouterService交互一起管理多媒体的播放行为，并维护当前已经配对上的remote
    // display设备，包括Wifi diplay、蓝牙A2DP设备、chromecast设备。
    private MediaRouter mMediaRouter;// MediaRouter提供了快速获得系统中用于演示（presentations）默认显示设备的方法。
    private DemoPresentation mPresentation;
    private boolean mPaused;
    private boolean change=false;
    private XWalkView mWebView;
    BroadcastReceiver bcr;
    private static final int IS_FINISH = 0x0001;
    private static final int RELOAD_URL = 0x0002;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"handleMessage = "+msg.what);
            if(msg.what==IS_FINISH) {
                mPresentation.webView.loadUrl("javascript:try{autoplay();}catch(e){}");//播放视频
            }else if(msg.what==RELOAD_URL) {
                if(change){
                //    mPresentation.webView.reload(XWalkView.RELOAD_NORMAL);
                    mPresentation.webView.loadUrl("file:///android_asset/index.html");

                    mWebView.load("file:///android_asset/index_one.html",null);
                    change=false;
                }else{
                    mPresentation.webView.loadUrl("file:///android_asset/index_one.html");
                    mWebView.load("file:///android_asset/index_one.html",null);
                    change=true;
                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*
         * 获取到媒体路由，当媒体路由被选择或取消选择或者路由首选的presentation显示屏幕发生变化时，
         * 它都会发送通知消息。一个应用程序可以非常简单通过地观察这些通知消息来自动地在首选的presentation
         * 显示屏幕上显示或隐藏一个presentation。
         */
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
       // setContentView(R.layout.activity_main);
        setContentView(R.layout.main_webview);

        //实例化广播接收器对象
        bcr = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("test", "^-^, Have received Massage!");
                Message reloadMsg=new Message();
                reloadMsg.what=PresentationWithMediaRouterActivity.RELOAD_URL;
                mHandler.sendMessage(reloadMsg);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("ABC");
        this.registerReceiver(bcr, filter);
    }

    public MediaPlayer.OnErrorListener videoErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // 播放出错处理
            Log.d(TAG,"OnErrorListener  ~~~~");

            return true;
        }
    };

    private void initWebView(){
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        if(mWebView==null){
            mWebView = (XWalkView ) findViewById(R.id.webviewMain);
            // mInfoTextView = (TextView) findViewById(R.id.info);
         //   mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        //    mWebView.setWebViewClient(new InnerWebViewClient());
            mWebView.setResourceClient(new GameWebViewClient(mWebView));
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mWebView.getSettings().setSupportZoom(true);
          //  mWebView.getSettings().setAppCacheEnabled(true);
      //      mWebView.getSettings().setDatabaseEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            mWebView.setWebChromeClient(new WebChromeClient() {
//                /**
//                 * 显示自定义视图，无此方法视频不能播放
//                 */
//                @Override
//                public void onShowCustomView(View view, CustomViewCallback callback) {
//                    super.onShowCustomView(view, callback);
//                }
//            });

               mWebView.load("file:///android_asset/index_one.html",null);
     //  mWebView.load("http://sda.4399.com/4399swf/upload_swf/ftp20/ssj/20170203/t6/index.html",null);
           //  mWebView.loadUrl("https://testdrive-archive.azurewebsites.net/Performance/FishIETank/",null);
          //     mWebView.loadUrl("file:///android_asset/index_one.html");
          //      mWebView.loadUrl("https://testdrive-archive.azurewebsites.net/Performance/FlyingImages/");
       //      mWebView.loadUrl("http://www.baidu.com");
            //       mWebView.loadUrl("https://testdrive-archive.azurewebsites.net/Performance/FishBowl/");
        }
    }

    class GameWebViewClient extends XWalkResourceClient{

        public GameWebViewClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);

            Log.d(TAG,"GameWebViewClient onLoadFinished~~~~");
         //   Message seg=new Message();
        //    seg.what = PresentationWithMediaRouterActivity.IS_FINISH;
       //     mHandler.sendMessageDelayed(seg,3000);

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        initWebView();
        /**
         * 注册一个具体的MediaRouter.Callback回调对象，并用来启动与特定MediaRouteSelector相匹配的媒体路由的发现
         * ， 以及监听发现的媒体路由的相关事件，如用户已选择连接到某个媒体路由设备、某个媒体路由设备的特性发生改变或者断开某个媒体路由等事件。
         * 应用为了使用相关的媒体路由，必须调用该函数来启动媒体路由的发现，并通过登记的回调函数接收相关的事件。
         */
        // 设置对媒体路由变化的监听
        mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO,
                mMediaRouterCallback);
        mPaused = false;
        updatePresentation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"oopos onPause ~~~~");
        // 移除回调监听
        mMediaRouter.removeCallback(mMediaRouterCallback);
//        mPaused = true;
//        if (mPresentation != null) {
//           // mPresentation.webView.onDestroy();
//            mPresentation.webView=null;
//            mPresentation.dismiss();
//            mPresentation = null;
//        }
//        if(mWebView !=null){
//            mWebView.onDestroy();
//            mWebView=null;
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"oopos onStop ~~~~");
        // 当Activity不可见时，清除Presentation
        if (mPresentation != null) {
          //  mPresentation.webView.onDestroy();
            mPresentation.webView=null;
            mPresentation.dismiss();
            mPresentation = null;
        }
        if(mWebView !=null){
            mWebView.onDestroy();;
            mWebView=null;
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // 加载菜单
        getMenuInflater().inflate(R.menu.presentation_with_media_router_menu,
                menu);

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.menu_media_route);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider)mediaRouteMenuItem.getActionProvider();
        mediaRouteActionProvider.setRouteTypes(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

        // 显示菜需要返回true
        return true;
    }

    /**
     * 代码示例展示了Presentation实现对象的作为一个单独方法的控制层.当一个显示器处理不可选状态或者失去联系时，该方法负责清除无效的展示对象，
     * 而在一个显示设备连接时负责创建一个展示对象。
     *
     * @description：
     * @author ldm
     * @date 2016-6-4 上午9:34:05
     */
    private void updatePresentation() {
        // 获取当前路由
        MediaRouter.RouteInfo route = mMediaRouter
                .getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        // 判断route信息是否为空，如果不为空则返回被选择演示（presentation）设备。该方法只对
        // route信息类型为ROUTE_TYPE_LIVE_VIDEO有效。
        Display presentationDisplay = route != null ? route
                .getPresentationDisplay() : null;

        // 清除无用的展示对象
        if (mPresentation != null
                && mPresentation.getDisplay() != presentationDisplay) {
            Log.i(TAG,
                    "Dismissing presentation because the current route no longer "
                            + "has a presentation display.");
            mPresentation.dismiss();
            mPresentation = null;
        }

        // 根据需要显示展示 对象
        if (mPresentation == null && presentationDisplay != null) {
            Log.i(TAG, "Showing presentation on display: "
                    + presentationDisplay);
            ;
            Log.i(TAG, "xxxxxx w: "+presentationDisplay.getWidth()+" h: "+presentationDisplay.getHeight());
            mPresentation = new DemoPresentation(this, presentationDisplay);
           // mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                Log.w(TAG,
                        "Couldn't show presentation!  Display was removed in "
                                + "the meantime.", ex);
                mPresentation = null;
            }
            mPresentation.setOwnerActivity(this);
            // 更新Activity中内容
            updateContents();
        }


    }

    /**
     * 更新内容
     *
     * @description：
     * @author ldm
     * @date 2016-6-4 上午9:27:40
     */
    private void updateContents() {

    }


    private final MediaRouter.SimpleCallback mMediaRouterCallback = new MediaRouter.SimpleCallback() {
        // 当用户连接到一个媒体路由输出设备上时调用。
        @Override
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteSelected: type=" + type + ", info=" + info);
            updatePresentation();
        }

        // 当用户断开一个媒体路由输出设备时调用。
        @Override
        public void onRouteUnselected(MediaRouter router, int type,
                                      MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteUnselected: type=" + type + ", info=" + info);
            updatePresentation();
        }

        // 当展示的显示器改变现实像素，如从720p变到1080p分辨率。
        @Override
        public void onRoutePresentationDisplayChanged(MediaRouter router,
                                                      MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
            updatePresentation();
        }
    };

//    /**
//     * Listens for when presentations are dismissed.
//     */
//    private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            if (dialog == mPresentation) {
//                Log.i(TAG, "Presentation was dismissed.");
//                mPresentation = null;
//                updateContents();
//            }
//        }
//    };

    /**
     * 要为辅助显示屏创建独特的内容，您需要扩展Presentation类，并实现onCreate()回调方法。在onCreate()中，
     * 调用setContentView()来指定您要在辅助显示屏上显示的UI。
     * 作为Dialog类的扩展，Presentation类提供了一个区域，在其中， 您的应用可以在辅助显示屏上显示不同的UI。
     *
     * @description：
     * @author ldm
     * @date 2016-6-4 上午9:08:45
     */
    private final class DemoPresentation extends Presentation {

       private WebView  webView;
        //  private XWalkView  webView;

        public DemoPresentation(Context context, Display display) {
            super(context, display);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // 必须要写下面这句话，调用父类的onCreate();
            super.onCreate(savedInstanceState);
            // 设置布局
           // setContentView(R.layout.presentation_with_media_router_content);


            setContentView(R.layout.webview_api);
            webView = (WebView )findViewById(R.id.webviewxxx);
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
            webView.setWebViewClient(new InnerWebViewClient());

        //    setContentView(R.layout.webview);
         //   webView = (XWalkView ) findViewById(R.id.webviewxxx);

            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setSupportZoom(true);
           // webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webView.setWebChromeClient(new WebChromeClient() {
                /**
                 * 显示自定义视图，无此方法视频不能播放
                 */
                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {
                    super.onShowCustomView(view, callback);
                }
            });
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
     //       webView.setResourceClient(new MeidiaWebViewClient(webView));
            webView.loadUrl("file:///android_asset/index_one.html");
          //  webView.loadUrl("http://sda.4399.com/4399swf/upload_swf/ftp20/ssj/20170203/t6/index.html");
            // webView.loadUrl("http://cloudgames.com/games/html5/zball5-new-en-s-iga-cloud/index.html?pub=23");
          //  webView.loadUrl("http://www.baidu.com");
           // webView.loadUrl("http://7xvl2z.com1.z0.glb.clouddn.com/nigg2.mp4");

        }

    }


    class MeidiaWebViewClient extends XWalkResourceClient{

        public MeidiaWebViewClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);

            Log.d(TAG,"MeidiaWebViewClient onLoadFinished~~~~");
          //  Message seg=new Message();
          //  seg.what = PresentationWithMediaRouterActivity.IS_FINISH;
           // mHandler.sendMessageDelayed(seg,3000);
        }


    }

    private static class InnerWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /**
         * 处理ssl请求
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /**
         * 页面载入完成回调
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG,"onPageFinished xxx!!!");
            view.loadUrl("javascript:try{autoplay();}catch(e){}");//播放视频
          //  uichange();
        }
    }
}