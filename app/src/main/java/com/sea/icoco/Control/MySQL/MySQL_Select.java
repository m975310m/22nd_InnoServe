package com.sea.icoco.Control.MySQL;

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

import static com.sea.icoco.MainActivity.dataControler;

/**
 * Created by AndyChuo on 2016/4/12.
 */
public class MySQL_Select extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    protected String doInBackground(String... arg0)
    {
        try
        {
            String sql = arg0[0];
//            Log.d("Debug MySQL",sql + " ...");
            String link = "http://"+dataControler.server+"/iCoCo/MySQL_Select.php";
            String data = URLEncoder.encode("sql", "UTF-8") + "=" + URLEncoder.encode(sql, "UTF-8");
            Log.d("Select debug",sql);
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
            JSONArray jsonArray = new JSONArray(sb2.toString());
            for (int i = 0 ; i < jsonArray.length() ; i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (int j = 0 ; j < jsonObject.names().length(); j++)
                {
                    if (jsonObject.getString(jsonObject.names().getString(j)).equals("null"))
                        jsonObject.put(jsonObject.names().getString(j),"");
                }
            }
            Log.d("Mysql","Result = "+sb2.toString());
            return jsonArray.toString();
        } catch (Exception e)
        {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        //在此複寫方法
    }

}
