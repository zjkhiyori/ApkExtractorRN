package com.appextractor.modules.apkextractor;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ApkExtractorModule extends ReactContextBaseJavaModule {
    private PackageManager packageManager;
    private List<PackageInfo> allAppList, userAppList, systemAppList;

    public ApkExtractorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        packageManager = reactContext.getPackageManager();
        allAppList = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
    }

    @Override
    public String getName() {
        return "ApkExtractorModule";
    }

    @ReactMethod
    public void getAllApp(Promise promise) {
        promise.resolve(parseArray(allAppList));
    }

    @ReactMethod
    public void getUserApp(Promise promise) {
        if(userAppList == null) {
            userAppList = new ArrayList<>();
            for (PackageInfo packageInfo : allAppList) {
                if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    userAppList.add(packageInfo);
                }
            }
        }
        promise.resolve(parseArray(userAppList));
    }

    @ReactMethod
    public void getSystemApp(Promise promise) {
        if(systemAppList == null) {
            systemAppList = new ArrayList<>();
            for (PackageInfo packageInfo : allAppList) {
                if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    systemAppList.add(packageInfo);
                }
            }
        }
        promise.resolve(parseArray(systemAppList));
    }

    private WritableArray parseArray(List<PackageInfo> list) {
        WritableArray writableArray = Arguments.createArray();
        for (PackageInfo packageInfo : list) {
            WritableMap writableMap = Arguments.createMap();
            writableMap.putString("appName", packageInfo.applicationInfo.loadLabel(packageManager).toString());
            writableMap.putString("appIcon", Drawable2Base64(packageInfo.applicationInfo.loadIcon(packageManager)));
            writableMap.putString("appPackageName", packageInfo.packageName);
            writableArray.pushMap(writableMap);
        }
        return writableArray;
    }


    private String Drawable2Base64(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(  0,   0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,  100, bos);
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
