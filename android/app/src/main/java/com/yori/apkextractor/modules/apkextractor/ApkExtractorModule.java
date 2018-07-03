package com.yori.apkextractor.modules.apkextractor;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class ApkExtractorModule extends ReactContextBaseJavaModule {
    private PackageManager packageManager;
    private List<PackageInfo> allAppList, userAppList, systemAppList;
    private ReactContext reactContext;

    private static final String BACKUP_PATH = "/sdcard/Download/";
    private static final String APK = ".apk";

    public ApkExtractorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        packageManager = reactContext.getPackageManager();
        allAppList = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
    }

    @Override
    public String getName() {
        return "ApkExtractorModule";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<>();
        constants.put("deviceLocale", getLocale());
        return constants;
    }

    private String getLocale() {
        Locale current = getReactApplicationContext().getResources().getConfiguration().locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return current.toLanguageTag();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(current.getLanguage());
            if (current.getCountry() != null) {
                builder.append("-");
                builder.append(current.getCountry());
            }
            return builder.toString();
        }
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

    @ReactMethod
    public void copyApp(String packageName, final String appName, int packageType, final Promise promise) {
        PackageInfo packageInfo = null;
        switch (packageType) {
            case 0: {
                // UserApp
                packageInfo = parsePackageInfo(packageName, userAppList);
                break;
            }
            case 1: {
                // SystemApp
                packageInfo = parsePackageInfo(packageName, systemAppList);
                break;
            }
            case 2: {
                // AllApp
                packageInfo = parsePackageInfo(packageName, allAppList);
                break;
            }
            default: {
                promise.reject( "unknown package type");
            }
        }
        if(packageInfo == null){
            promise.reject("packageInfo not found");
            return;
        }
        final PackageInfo finalPackageInfo = packageInfo;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doCopy(finalPackageInfo, appName, promise);
            }
        }).start();
    }

    @ReactMethod
    public void showSnackbar(String message, String actionText, final Callback callback) {
        ViewGroup view = (ViewGroup) getCurrentActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(actionText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.invoke();
                    }
                }).show();
    }

    @ReactMethod
    public void share(String filePath, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        intent.setType("application/octet-stream");
        Intent chooser = Intent.createChooser(intent, title);
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reactContext.startActivity(chooser);
    }

    private PackageInfo parsePackageInfo(String packageName, List<PackageInfo> list) {
        for (PackageInfo packageInfo : list) {
            if(packageName.equals(packageInfo.packageName)) {
                return packageInfo;
            }
        }
        return null;
    }

    private void doCopy(PackageInfo packageInfo, String appName, Promise promise) {
        String source = packageInfo.applicationInfo.sourceDir;
        String dest = BACKUP_PATH + appName + APK;
        try {
            if (!new File(BACKUP_PATH).exists()) {
                new File(BACKUP_PATH).mkdir();
            }
            File destFile = new File(dest);
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.createNewFile();

            FileInputStream in = new FileInputStream(new File(source));
            FileOutputStream out = new FileOutputStream(destFile);
            FileChannel inC = in.getChannel();
            FileChannel outC = out.getChannel();
            int length;
            while (true) {
                if (inC.position() == inC.size()) {
                    inC.close();
                    outC.close();
                    promise.resolve(dest);
                    break;
                }
                if ((inC.size() - inC.position()) < 1024 * 1024) {
                    length = (int) (inC.size() - inC.position());
                } else {
                    length = 1024 * 1024;
                }
                inC.transferTo(inC.position(), length, outC);
                inC.position(inC.position() + length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,  100, bos);
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
