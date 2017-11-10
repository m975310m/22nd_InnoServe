package com.sea.icoco.Control.BankAPI.ESUN.Bank;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Base64;
/**
 * Created by AndyChuo on 2016/4/12.
 */
public class ESUN_Login extends AsyncTask<String, Void, String>
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
    public ESUN_Login()
    {
        success=false;
    }

    protected void onPreExecute()
    {}

    protected String doInBackground(String... arg0)
    {
        UserID = arg0[0];
        PIN = arg0[1];

        StringBuilder sb = new StringBuilder();

        String link = "https://eospu.esunbank.com.tw/esun/bank/public/v1/user/oauth/authorize";


        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setRequestProperty("Authorization","Basic "+dataControler.esunData.Authorization);
            urlConnection.connect();

            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(UserID, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(PIN, "UTF-8");
            data += "&" + URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("password", "UTF-8");
            data += "&" + URLEncoder.encode("scope", "UTF-8") + "=" + URLEncoder.encode("/public/v1/user", "UTF-8");

            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
            out.write(data);
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
        } finally{
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
                JSONObject ESUNLoginData = new JSONObject(result);
                if (!ESUNLoginData.getString("access_token").isEmpty())
                {
                    Log.d("Debug CTBC Login","ESUN Login Success !!");
                    dataControler.esunData.setToken(ESUNLoginData);
                }
            } catch (JSONException e)
            {
                Log.e("Debug ESUN Login",result);
                Log.e("Debug ESUN Login",e.toString());
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
