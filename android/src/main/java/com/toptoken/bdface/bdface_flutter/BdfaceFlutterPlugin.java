package com.toptoken.bdface.bdface_flutter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.utils.Base64Utils;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;


/** BdfaceFlutterPlugin */
public class BdfaceFlutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

  private static final String TAG = "bdface_flutter";
  private static final String CHANNEL = "toptoken.com/bdface_flutter";


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    pluginBinding = flutterPluginBinding;
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    Log.e(TAG,"注册百度人脸采集插件");
    activityBinding = binding;
    init(
            pluginBinding.getBinaryMessenger(),
            (Application) pluginBinding.getApplicationContext(),
            activityBinding.getActivity());
  }

  @Override
  public void onDetachedFromActivity() {
    clear();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    if (registrar.activity() == null) {
      // If a background flutter view tries to register the plugin, there will be no activity from the registrar,
      // we stop the registering process immediately because the ImagePicker requires an activity.
      return;
    }
    Activity activity = registrar.activity();
    Application application = null;
    if (registrar.context() != null) {
      application = (Application) (registrar.context().getApplicationContext());
    }
    BdfaceFlutterPlugin bdfaceFlutterPlugin = new BdfaceFlutterPlugin();
    bdfaceFlutterPlugin.init(registrar.messenger(),application,activity);

  }

  private MethodChannel channel;
  private FlutterPluginBinding pluginBinding;
  private ActivityPluginBinding activityBinding;
  private Application application;
  private Activity activity;

  public static BdfaceFlutterPlugin instance;

  public BdfaceFlutterPlugin(){
    instance = this;
  }

  private void init(final BinaryMessenger messenger, final Application application, final Activity activity){
    this.activity = activity;
    this.application = application;
    channel = new MethodChannel(messenger, CHANNEL);
    channel.setMethodCallHandler(this);

  }

  private void clear(){
    activityBinding = null;
    channel.setMethodCallHandler(null);
    channel = null;
    application = null;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    Log.e(TAG,"Channel 通道执行方法："+call.method);
    switch (call.method){
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "init":
        initLib(call,result);
        break;
      case "openFaceLive":
        openFaceLive(call,result);
        break;
      case "faceLogin":
        faceLogin(call,result);
        break;
      default:
        result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    pluginBinding = null;
  }

  /**
   * 初始化SDK
   */
  private void initLib(MethodCall call, Result result) {
    Log.e(TAG, "初始化百度AI人脸识别插件");
    String licenseID = call.argument("licenseID");
    String licenseFileName = call.argument("licenseFileName");
    FaceSDKManager.getInstance().initialize(activity, licenseID, licenseFileName);

  }

  //TODO 开始活体检测
  private void openFaceLive(MethodCall call,Result result){
    Intent intent = new Intent(activity, FaceLivenessExpActivity.class);
    activity.startActivity(intent);
  }

  //TODO 人脸登录
  private void faceLogin(MethodCall call,Result result){
    Intent intent = new Intent(activity, FaceDetectExpActivity.class);
    activity.startActivity(intent);
  }


  static void faceLiveResult(String resultCode,String base64Image){
    Log.e(TAG, "活体检测结果："+resultCode);
    Map<String,Object> resultMap = new HashMap();
    resultMap.put("imageByte",Base64Utils.decode(base64Image,Base64Utils.NO_WRAP));
    resultMap.put("resultCode",resultCode);
    BdfaceFlutterPlugin.instance.channel.invokeMethod("onFaceLiveResult",resultMap);
  }

  static void faceLoginResult(String resultCode,String base64Image){
    Log.e(TAG, "活体检测结果："+resultCode);
    Map<String,Object> resultMap = new HashMap();
    resultMap.put("imageByte",Base64Utils.decode(base64Image,Base64Utils.NO_WRAP));
    resultMap.put("resultCode",resultCode);
    BdfaceFlutterPlugin.instance.channel.invokeMethod("onFaceLoginResult",resultMap);
  }
}
