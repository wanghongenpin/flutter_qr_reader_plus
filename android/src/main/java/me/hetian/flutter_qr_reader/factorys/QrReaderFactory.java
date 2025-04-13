package me.hetian.flutter_qr_reader.factorys;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;
import me.hetian.flutter_qr_reader.views.QrReaderView;

public class QrReaderFactory extends PlatformViewFactory {

    private final BinaryMessenger binaryMessenger;

    public QrReaderFactory(@NonNull BinaryMessenger binaryMessenger) {
        super(StandardMessageCodec.INSTANCE);
        this.binaryMessenger = binaryMessenger;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PlatformView create(@NonNull Context context, int id, @Nullable Object args) {
        Map<String, Object> params = (Map<String, Object>) args;
        return new QrReaderView(context, binaryMessenger, id, params);
    }
}
