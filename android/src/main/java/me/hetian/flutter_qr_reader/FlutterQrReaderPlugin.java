package me.hetian.flutter_qr_reader;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import java.io.File;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import me.hetian.flutter_qr_reader.factorys.QrReaderFactory;


/** FlutterQrReaderPlugin */
public class FlutterQrReaderPlugin implements FlutterPlugin, MethodCallHandler {

//  private static final int REQUEST_CODE_CAMERA_PERMISSION = 3777;
  private static final String CHANNEL_NAME = "me.hetian.flutter_qr_reader";
  private static final String CHANNEL_VIEW_NAME = "me.hetian.flutter_qr_reader.reader_view";

  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
    channel.setMethodCallHandler(this);

    binding.getPlatformViewRegistry().registerViewFactory(CHANNEL_VIEW_NAME, new QrReaderFactory(binding.getBinaryMessenger()));
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

//  private interface PermissionsResult {
//    void onSuccess();
//    void onError();
//  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("imgQrCode")) {
      imgQrCode(call, result);
    } else {
      result.notImplemented();
    }
  }

  @SuppressLint("StaticFieldLeak")
  void imgQrCode(MethodCall call, final Result result) {
    final String filePath = call.argument("file");
    if (filePath == null) {
      result.error("Not found data", null, null);
      return;
    }
    File file = new File(filePath);
    if (!file.exists()) {
      result.error("File not found", null, null);
    }

    new AsyncTask<String, Integer, String>() {
      @Override
      protected String doInBackground(String... params) {
        // 解析二维码/条码
        return QRCodeDecoder.syncDecodeQRCode(filePath);
      }
      @Override
      protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(null == s){
          result.error("not data", null, null);
        }else {
          result.success(s);
        }
      }
    }.execute(filePath);
  }

//  @TargetApi(Build.VERSION_CODES.M)
//  private void checkPermissions(final PermissionsResult result) {
//    if (!(registrar.activity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
//      registrar.addRequestPermissionsResultListener(new PluginRegistry.RequestPermissionsResultListener() {
//        @Override
//        public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//          if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
//            for (int i = 0; i < permissions.length; i++) {
//              String permission = permissions[i];
//              int grantResult = grantResults[i];
//
//              if (permission.equals(Manifest.permission.CAMERA)) {
//                if (grantResult == PackageManager.PERMISSION_GRANTED) {
//                  result.onSuccess();
//                } else {
//                  result.onError();
//                }
//              }
//            }
//          }
//          return false;
//        }
//      });
//      registrar.activity().requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
//    } else {
//      result.onSuccess();
//    }
//  }
}
