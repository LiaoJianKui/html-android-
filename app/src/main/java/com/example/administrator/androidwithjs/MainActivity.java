package com.example.administrator.androidwithjs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button mbtn1, mbtn2;
    private WebView mWebView;
    private final int REQUEST_CODE_CALLPHONE = 0;
    private final int REQUEST_CODE_SENDMSM=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mbtn1 = (Button) findViewById(R.id.btn1);
        mbtn2 = (Button) findViewById(R.id.btn2);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/web.html");
        mWebView.addJavascriptInterface(MainActivity.this, "android");
        mbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:javacalljs()");
            }
        });
        mbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:javacalljswith(" + "'http://blog.csdn.net/Leejizhou'" + ")");

            }
        });
    }

    @JavascriptInterface
    public void startFunction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "show", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

//调用打电话的功能
    @JavascriptInterface
    public void callPhone( String telphone) {
        if (!hasCallPermission()) {
            applyCallPhonePermission();
        }
        if(TextUtils.isEmpty(telphone)){
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
            return;

        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + telphone));
        startActivity(intent);
    }

    //调用发短信的功能
    public void sendMessage(String msgTelphone,String msgText){
        if(!hasSendMessagePermission()){
            applyCallPhonePermission();
        }
        if(TextUtils.isEmpty(msgTelphone)||TextUtils.isEmpty(msgText)){
            Toast.makeText(this, "手机号码或短信内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SmsManager msg=SmsManager.getDefault();
        ArrayList<String> list=msg.divideMessage(msgText);
        for(String text:list){
            msg.sendTextMessage(msgTelphone,null,msgText,null,null);
        }
        Toast.makeText(this, "短信发送成功", Toast.LENGTH_SHORT).show();

    }

    //检查是否有打电话的权限
    private boolean hasCallPermission() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
    }
    //检查是否有发短信的权限
    private boolean hasSendMessagePermission(){
        return PackageManager.PERMISSION_GRANTED==ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS);
    }

    //申请打电话的权限和申请发短信的权限
    private void applyCallPhonePermission() {
        String[] permissions = {Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE_CALLPHONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CALLPHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "申请打电话的权限成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "申请打电话的权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_SENDMSM:
                if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "申请发短信的权限成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "申请发短信的权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
