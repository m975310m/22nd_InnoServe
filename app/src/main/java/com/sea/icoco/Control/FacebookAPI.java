package com.sea.icoco.Control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/30.
 */
/*
    Use before dosomething...

        AndroidManifest.xml:
            <manifest>
                ...
                <uses-permission android:name="android.permission.INTERNET"/>
                <application>
                    ...
                    <meta-data android:name="com.facebook.sdk.ApplicationId" android:value="@string/facebook_app_id"/>
                    <!--FB MicrosoftVision Activity-->
                    <activity android:name="com.facebook.FacebookActivity"
                        android:configChanges=
                            "keyboard|keyboardHidden|screenLayout|screenSize|orientation"
                        android:theme="@android:style/Theme.Translucent.NoTitleBar"
                        android:label="@string/app_name" />
                    ...
                </application>
                ...
            </manifest>

        string.xml:
            <string name="facebook_app_id">1802946313285544</string>

        build.gradle:
            repositories {
                mavenCentral()
            }

            dependencies {
            ...
            compile 'com.facebook.android:facebook-android-sdk:[4,5)'
            }

    How to Use in Activity:
        import com.facebook.CallbackManager;
        import com.facebook.FacebookSdk;
        import com.facebook.MicrosoftVision.LoginManager;
        import java.util.Arrays;

        ...

        private CallbackManager callbackManager;
        public static FacebookAPI facebookAPI;
        private LoginManager loginManager;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            FacebookSdk.sdkInitialize(getApplicationContext()); //  FB初始化  必須初始化在第一行  否則錯誤

            ...

            createClass();
        }

        private void createClass()
        {
            facebookAPI = new FacebookAPI();
        }

        // FB 自訂按鈕指向此事件
        public void FbLoginEvent(View view)
        {
            loginManager = loginManager.getInstance();
            loginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends", "email", "user_photos"));
            callbackManager = CallbackManager.Factory.create();
            facebookAPI.MicrosoftVision(loginManager,callbackManager);
            waitFbLogin()
        }

        //FB 成功接收到數據時，轉回自己的頁面繼續
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
            Log.d("Debug MainActivity",Integer.toString(requestCode)+","+ Integer.toString(resultCode));
        }

        //確保取得資料成功
        private void waitFbLogin()
        {
            final Handler waitToDo = new Handler();
            final Runnable runnable = new Runnable()
            {
                int delayMs = 500; // 檢查時間 (毫秒)
                int timeoutSec = 10; // 檢查時間上限 (秒)

                int count = 0;
                int maxCount = (timeoutSec*1000)/delayMs;
                public void run()
                {
                    if (count < maxCount)
                    {
                        if(facebookAPI.isLoginSuccess()) // 檢查事件
                        {
                            //檢查通過要做的事情
    //                        Log.d("debug",facebookAPI.getName());
                            count=maxCount;
                        }
                        count++;
                        waitToDo.postDelayed(this, delayMs);
                    }
                }
            };
            waitToDo.post(runnable);
        }

    成功!! 可以使用以下方法:
        FacebookAPI.getId();
        FacebookAPI.getName();
        FacebookAPI.getFriends();
        FacebookAPI.getEmail();
        FacebookAPI.getGender();
        FacebookAPI.getAll();
        FacebookAPI.getBirthday();
 */
public class FacebookAPI
{
    private AccessToken accessToken;
    private JSONObject all;
    private JSONArray friends_jarr;
    private String id,name,email = "" ,gender = "" ,birthday = "";
    private Context context;

    public void login(final LoginManager loginManager, CallbackManager callbackManager, Context context)
    {
        this.context = context;
//        Log.d("Debug FacebookAPI","FB Loging ...");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                accessToken = loginResult.getAccessToken();
                loginManager.logOut();
                loadData();
            }

            @Override
            public void onCancel()
            {
                Log.e("Debug FacebookAPI","onCancel");
            }

            @Override
            public void onError(FacebookException e)
            {
                Log.e("Debug FacebookAPI",e.toString());
            }
        });

    }

    private void loadData()
    {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response)
            {
                try
                {

                    all = object;
                    id = object.getString("id");
                    name = object.getString("name");

                    try
                    { email = object.getString("email"); }
                    catch(Exception e)
                    { Log.e("Debug FacebookAPI",e.toString()); }

                    try
                    { gender = object.getString("gender"); }
                    catch(Exception e)
                    { Log.e("Debug FacebookAPI",e.toString()); }

                    try
                    { birthday = object.getString("birthday"); }
                    catch(Exception e)
                    { Log.e("Debug FacebookAPI",e.toString()); }

                    friends_jarr = object.getJSONObject("friends").getJSONArray("data");

                    for (int i = 0 ; i < friends_jarr.length() ; i++)
                    {
                        JSONObject friend = friends_jarr.getJSONObject(i);
                        friend.put("pictureUrl","https://graph.facebook.com/"+friends_jarr.getJSONObject(i).get("id")+"/picture?type=large");
                    }

                    onLoginSuccess();
                    loadFriendsPicture();
                }
                catch (JSONException e)
                {
                    Log.e("Debug FacebookAPI",e.toString());
                    Log.e("Debug FacebookAPI",object.toString());
                    e.printStackTrace();
                }
            }

            private void loadFriendsPicture() throws JSONException
            {
                for (int i = 0 ; i < friends_jarr.length() ; i++)
                {
                    JSONObject friend = friends_jarr.getJSONObject(i);
                    new DownloadImageTask(friend).execute("https://graph.facebook.com/"+friends_jarr.getJSONObject(i).get("id")+"/picture?type=large");
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,friends");
        request.setParameters(parameters);
        request.executeAsync();
    }




    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public JSONArray getFriends()
    {
        return friends_jarr;
    }

    public String getEmail()
    {
        return email;
    }

    public String getGender() { return gender; }

    public String getBirthday()
    {
        return birthday;
    }

    public JSONObject getAll()
    {
        return all;
    }

    public void onLoginSuccess() throws JSONException
    { }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        JSONObject json;

        public DownloadImageTask(JSONObject json) {
            this.json = json;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Debug", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try
            {
                json.put("picture",result);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}

