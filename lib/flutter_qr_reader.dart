import 'dart:async';
import 'dart:ui';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class FlutterQrReader {
  static const MethodChannel _channel =
      const MethodChannel('me.hetian.flutter_qr_reader');

  static Future<String?> imgScan(String path) async {
    try {
      final rest = await _channel.invokeMethod("imgQrCode", {"file": path});
      return rest;
    } catch (e) {
      print(e);
      return null;
    }
  }
}

class QrReaderView extends StatefulWidget {
  final Function(QrReaderViewController) callback;

  final int autoFocusIntervalInMs;
  final bool torchEnabled;
  final double width;
  final double height;

  QrReaderView({
    required this.width,
    required this.height,
    required this.callback,
    this.autoFocusIntervalInMs = 500,
    this.torchEnabled = false,
  });

  @override
  _QrReaderViewState createState() => new _QrReaderViewState();
}

class _QrReaderViewState extends State<QrReaderView> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: "me.hetian.flutter_qr_reader.reader_view",
        creationParams: {
          "width": (widget.width * window.devicePixelRatio).floor(),
          "height": (widget.height * window.devicePixelRatio).floor(),
          "extra_focus_interval": widget.autoFocusIntervalInMs,
          "extra_torch_enabled": widget.torchEnabled,
        },
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: _onPlatformViewCreated,
        gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>[
          new Factory<OneSequenceGestureRecognizer>(
              () => new EagerGestureRecognizer()),
        ].toSet(),
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: "me.hetian.flutter_qr_reader.reader_view",
        creationParams: {
          "width": widget.width,
          "height": widget.height,
          "extra_focus_interval": widget.autoFocusIntervalInMs,
          "extra_torch_enabled": widget.torchEnabled,
        },
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: _onPlatformViewCreated,
        gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>[
          new Factory<OneSequenceGestureRecognizer>(
              () => new EagerGestureRecognizer()),
        ].toSet(),
      );
    } else {
      return Text('平台暂不支持');
    }
  }

  void _onPlatformViewCreated(int id) {
    widget.callback(QrReaderViewController(id));
  }

  @override
  void dispose() {
    super.dispose();
  }
}

typedef ReadChangeBack = void Function(String, List<Offset>);

class QrReaderViewController {
  final int id;
  final MethodChannel _channel;
  ReadChangeBack? onQrBack;

  QrReaderViewController(this.id)
      : _channel =
            MethodChannel('me.hetian.flutter_qr_reader.reader_view_$id') {
    _channel.setMethodCallHandler(_handleMessages);
  }

  Future _handleMessages(MethodCall call) async {
    switch (call.method) {
      case "onQRCodeRead":
        final List<Offset>  points = [] ;

        if (call.arguments.containsKey("points")) {
          final pointsStrs = call.arguments["points"];
          for (String point in pointsStrs) {
            final a = point.split(",");
            points
                .add(Offset(double.parse(a.first), double.parse(a.last)));
          }
        }

        this.onQrBack?.call(call.arguments["text"], points);
        break;
    }
  }

  // 打开手电筒
  Future<bool> setFlashlight() async {
    return (await _channel.invokeMethod("flashlight")) == true;
  }

  // 开始扫码
  Future startCamera(ReadChangeBack onQrBack) async {
    this.onQrBack = onQrBack;
    return _channel.invokeMethod("startCamera");
  }

  // 结束扫码
  Future stopCamera() async {
    return _channel.invokeMethod("stopCamera");
  }
}
