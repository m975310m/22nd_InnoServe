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
public class SavingAcctInq extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    public String Token;
    protected String doInBackground(String... arg0)
    {
        try
        {
            Token = dataControler.ctbcData.getToken();
//            Log.d("Debug MySQL",sql + " ...");
            String link = "http://"+dataControler.server+"/iCoCo/CTBC/SavingAcctInq.php";
            String data = URLEncoder.encode("Token", "UTF-8") + "=" + URLEncoder.encode(Token, "UTF-8");

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
        try
        {
            JSONObject BalanceData = new JSONObject(result);
            if (BalanceData.getString("ResponseCode").equals("0000"))
            {
//                Log.d("Debug CTBC BounsPoint","Get CTBC Bouns Point Data Success !!");
                dataControler.ctbcData.setBalance(BalanceData);
            }
        } catch (JSONException e)
        {
            Log.e("Debug Emulation Saving","result = "+result);
            Log.e("Debug Emulation Saving",e.toString());
            e.printStackTrace();
        }
    }
}
