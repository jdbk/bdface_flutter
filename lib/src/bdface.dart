import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:platform/platform.dart';

typedef Future<dynamic> EventHandler(Map<String,dynamic> params);

class Bdface{

  factory Bdface()=> _instance;
  final MethodChannel _channel;
  final Platform _platform;

  @visibleForTesting
  Bdface.private(MethodChannel channel, Platform platform)
      : _channel = channel,
        _platform = platform;

  static final Bdface _instance = new Bdface.private(
      const MethodChannel('toptoken.com/bdface_flutter'),
      const LocalPlatform());

  EventHandler _onFaceLive;
  EventHandler _onFaceLogin;

  //TODO 创建监听
  void addEventHandler({EventHandler onFaceLive,EventHandler onFaceLogin}){
    _onFaceLive = onFaceLive;
    _onFaceLogin = onFaceLogin;
    _channel.setMethodCallHandler(_handleMethod);
  }

  Future<Null> _handleMethod(MethodCall call) async{
    switch(call.method){
      case 'onFaceLiveResult':
        return _onFaceLive(call.arguments.cast<String,dynamic>());
      case 'onFaceLoginResult':
        return _onFaceLogin(call.arguments.cast<String,dynamic>());
      default:
        throw new UnsupportedError("Unrecognized Event");
    }
  }
  
  //TODO 初始化
  Future<void> initLib(licenseID,licenseFileName) async{
    final Map<String, dynamic> arguments = <String, dynamic>{
      'licenseID': licenseID,
      'licenseFileName': licenseFileName,
    };
    await _channel.invokeMethod('init', arguments);
  }

  //TODO 人脸检测
  Future<void> openFaceLive() async{
    await _channel.invokeMethod('openFaceLive');
  }


  //TODO 人脸登录
  Future<void> faceLoginLive() async{
    await _channel.invokeMethod('faceLogin');
  }

}