package com.sea.icoco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//FB API
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sea.icoco.Control.Action.AutoDetectionCoupon_Budget;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.FacebookAPI;
import com.sea.icoco.Control.MySQL.loginSelect;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity
{
    public static MyFacebook myFacebookAPI;
    public static DataControler dataControler;
    public static AutoDetectionCoupon_Budget autoDetectionCoupon_budget;
    private CallbackManager callbackManager;
    private LoginManager loginManager;


//    GCM
//只要把您的GCM註冊ID填入，其他部分皆一樣
    private String SENDER_ID = "292098287069";
    private static final String TAG = "GCM :";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static boolean initMapActivity = false;
    public static boolean initDiaryMapActivity = false;
    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private Context context = this;
    private String regid;
//    GCM

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        FacebookSdk.sdkInitialize(getApplicationContext()); //  FB初始化  必須初始化在第一行  否則錯誤
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createClass();
        gcm();
    }



    private void createClass()
    {
        myFacebookAPI = new MyFacebook();
        dataControler = new DataControler(this);
        autoDetectionCoupon_budget = new AutoDetectionCoupon_Budget();
    }

    public void FbLoginEvent(View view)
    {
        loginManager = loginManager.getInstance();
        loginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_photos", "user_friends"));
        callbackManager = CallbackManager.Factory.create();
        myFacebookAPI.login(loginManager, callbackManager,MainActivity.this);
    }

    //FB 成功接收到數據時，轉回自己的頁面繼續
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //我的facebook 重寫登入成功方法
    private class MyFacebook extends FacebookAPI
    {
        public void onLoginSuccess() throws JSONException
        {
            new loginSelect(MainActivity.this,MenuActivity.class).execute("SELECT * FROM icoco.user where `fbid` = '"+getId()+"';");
//            new loginSelect(MainActivity.this,MapsActivity.class).execute("SELECT * FROM icoco.user where `fbid` = '"+getId()+"'");
            dataControler.friendsData.loadFbFriends(getFriends());
            Log.d("Debug FacebookAPI","FB Login Success !!");
        }
    }

    //以下GCM---------------------------

    private void gcm()
    {
        //確認是否已註冊過裝置，將這段放入您欲註冊的位置
        if (checkPlayServices())
        {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty())
            {
                registerInBackground();
            }
            else
            {
                Log.i(TAG,"registration ID = "+regid);
            }
        }
        else
        {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty())
        {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);

                    msg = "Device registered, registration ID=" + regid;
                    Log.i(TAG,"registration ID = "+regid);
                    sendRegistrationIdToBackend();
                    storeRegistrationId(context, regid);
                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
            }
        }.execute(null, null, null);
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences(Context context)
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private void sendRegistrationIdToBackend()
    {

        //裝置向GCM註冊完畢時進入
    }
}




