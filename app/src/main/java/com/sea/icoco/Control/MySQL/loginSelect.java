package com.sea.icoco.Control.MySQL;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.FacebookAPI;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;

//註冊查詢 重寫接收完成方法
public class loginSelect extends MySQL_Select
{
    Context context;
    Class aClass;
    DataControler dataControler = MainActivity.dataControler;
    FacebookAPI facebookAPI = MainActivity.myFacebookAPI;

    public loginSelect(Context context, Class c)
    {
        this.context = context;
        this.aClass = c;
    }

    protected void onPostExecute(String result)
    {
        if (!result.isEmpty())
        {
            try
            {
                if (!result.equals("[]")) // 有查到資料註冊資料
                {
                    dataControler.userData.login(new JSONArray(result).getJSONObject(0),context,aClass);
                }
                else  // 沒有資料則註冊
                {
                    dataControler.userData.signup(facebookAPI.getAll(),context,aClass);
                }
            }
            catch (JSONException e)
            {
                Log.e("Select Debug",e.toString());
                Log.e("Select Debug",result);
            }
        }
        else
        {
            Log.e("Select Debug","接收失敗");
        }
    }
}
