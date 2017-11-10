package com.sea.icoco.Control.BankAPI.Emulation;

import android.os.AsyncTask;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by AndyChuo on 2016/4/12.
 */
public class Login extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    public String CustID,UserID,PIN;
    protected String doInBackground(String... arg0)
    {
        try
        {
            CustID = arg0[0];
            UserID = arg0[1];
            PIN = arg0[2];
//            Log.d("Debug MySQL",sql + " ...");
            String link = "http://"+dataControler.server+"/iCoCo/CTBC/Login.php";
            String data = URLEncoder.encode("CustID", "UTF-8") + "=" + URLEncoder.encode(CustID, "UTF-8");
            data += "&" + URLEncoder.encode("UserID", "UTF-8") + "=" + URLEncoder.encode(UserID, "UTF-8");
            data += "&" + URLEncoder.encode("PIN", "UTF-8") + "=" + URLEncoder.encode(PIN, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            String[] arr = sb.toString().split("<br />");
            StringBuilder sb2 = new StringBuilder();
            for (int i=0;i<arr.length;i++)
            {
                sb2.append(arr[i]);
            }
            return sb2.toString();
        } catch (Exception e)
        {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(!result.isEmpty())  //檢查是否為空　如果不為空
        {
            try
            {
                JSONObject LoginData = new JSONObject(result);
                if (!LoginData.getString("Token").isEmpty())
                {
                    Log.d("Debug Emulation Login","Emulation Login Success !!");
                    dataControler.ctbcData.setToken(LoginData);
                }
            } catch (JSONException e)
            {
                Log.e("Debug Emulation Login",e.toString());
                e.printStackTrace();
            }
        }
    }
}
