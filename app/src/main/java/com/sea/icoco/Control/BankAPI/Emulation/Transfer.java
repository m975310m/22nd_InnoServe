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
public class Transfer extends AsyncTask<String, Void, String>
{
//     MySQL_Select().execute("SQL指令");
//     範例 new MySQL_Select().execute("Select * from userlist");
//     success = true;
//     回傳 JSONARRAY 的 "字串"
//    搭配 new JSONArray((String) result) 即可變為JSONARRAY
    DataControler dataControler = MainActivity.dataControler;
    protected void onPreExecute()
    {}

    protected String doInBackground(String... arg0)
    {
        try
        {
            String Token = dataControler.ctbcData.getToken();
            String receiveID = arg0[0];
            String TranAmt = arg0[1];
//            Log.d("Debug MySQL",sql + " ...");
            String link = "http://"+dataControler.server+"/iCoCo/CTBC/Transfer.php";
            String data = URLEncoder.encode("Token", "UTF-8") + "=" + URLEncoder.encode(Token, "UTF-8");
            data += "&" + URLEncoder.encode("receiveID", "UTF-8") + "=" + URLEncoder.encode(receiveID, "UTF-8");
            data += "&" + URLEncoder.encode("TranAmt", "UTF-8") + "=" + URLEncoder.encode(TranAmt, "UTF-8");

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

    }

}
