package com.sea.icoco.Control.BankAPI.CTBC;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by AndyChuo on 2016/4/12.
 */
public class CTBC_Login extends AsyncTask<String, Void, String>
{
//     MySQL_Select().execute("SQL指令");
//     範例 new MySQL_Select().execute("Select * from userlist");
//     success = true;
//     回傳 JSONARRAY 的 "字串"
//    搭配 new JSONArray((String) result) 即可變為JSONARRAY


    public String result = "",CustID="",UserID="",PIN="";
    private boolean success;
    private Context context;
    DataControler dataControler = MainActivity.dataControler;
    //flag 0 means get and 1 means post.(By default it is get.)
    public CTBC_Login()
    {
        success=false;
    }

    protected void onPreExecute()
    {}

    protected String doInBackground(String... arg0)
    {
        CustID = arg0[0];
        UserID = arg0[1];
        PIN = arg0[2];

        StringBuilder sb = new StringBuilder();

        String link = "http://52.69.27.105:8080/hackathon/login";


        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Host", "52.69.27.105:8080");
            urlConnection.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("CustID", CustID);
            jsonParam.put("UserID", UserID);
            jsonParam.put("PIN", PIN);
            jsonParam.put("Token","123");
            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            int HttpResult =urlConnection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
            }else{
                return urlConnection.getResponseMessage();
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(!result.isEmpty())  //檢查是否為空　如果不為空
        {
            try
            {
                JSONObject CTBCLoginData = new JSONObject(result);
                if (!CTBCLoginData.getString("Token").isEmpty())
                {
                    Log.d("Debug CTBC Login","CTBC Login Success !!");
                    dataControler.ctbcData.setToken(CTBCLoginData);
                }
            } catch (JSONException e)
            {
                Log.e("Debug CTBC Login",e.toString());
                e.printStackTrace();
            }
        }
    }

    public boolean getIsSuccess()
    {
        return success;
    }
    public String getResult_String()
    {
        return result;
    }
    public JSONArray getResult_JsonArray() throws JSONException
    {
        return new JSONArray(result);
    }
}
