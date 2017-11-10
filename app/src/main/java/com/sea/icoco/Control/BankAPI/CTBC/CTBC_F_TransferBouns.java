package com.sea.icoco.Control.BankAPI.CTBC;

import android.os.AsyncTask;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/9/7.
 */
public class CTBC_F_TransferBouns extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    String receiveID;
    @Override
    protected String doInBackground(String... strings)
    {
        try
        {
            String Token = strings[0];
            receiveID = strings[1];
            String TranAmt = strings[2];
            String link = "http://"+dataControler.server+"/iCoCo/CTBC/TransferBounsPoint.php";
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

            String sb2 = sb.toString().replace("<br />","");
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
            JSONObject CTBCBounsPointData = new JSONObject(result);
            if (CTBCBounsPointData.getString("RspCode").equals("0000"))
            {
//                Log.d("Debug CTBC BounsPoint","Get CTBC Bouns Point Data Success !!");
                dataControler.ctbcData.setBounsPoint(CTBCBounsPointData);
                String sql = "UPDATE `icoco`.`userupdate` SET `PointValue`='1' WHERE `uid`='"+receiveID+"';";
                new MySQL_Execute().execute(sql);
            }
        } catch (JSONException e)
        {
            Log.e("Debug F_TransferBouns","result = "+result);
            Log.e("Debug F_TransferBouns",e.toString());
            e.printStackTrace();
        }
    }
}
