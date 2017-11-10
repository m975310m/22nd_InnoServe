package com.sea.icoco.Control.BankAPI.CTBC;

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

/**
 * Created by Administrator on 2016/9/6.
 */
public class CTBC_CreditCardLimit extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    @Override
    protected String doInBackground(String... strings)
    {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection=null;
        try
        {
            String link = "http://52.69.27.105:8080/hackathon/CreditCardLimit";
//            String data = URLEncoder.encode("Token", "UTF-8") + "=" + URLEncoder.encode(Token, "UTF-8");
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
            jsonParam.put("CustID", dataControler.ctbcData.getCustID());
            jsonParam.put("Token", dataControler.ctbcData.getToken());

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
        try
        {
            JSONObject CreditCardLimit = new JSONObject(result);
            if (CreditCardLimit.getString("ResponseCode").equals("0000"))
            {
                JSONArray CreditCardData = CreditCardLimit.getJSONArray("CreditCardLimit");
//                Log.d("Debug CTBC BounsPoint","Get CTBC Bouns Point Data Success !!");
                dataControler.ctbcData.setCreditCardLimit(CreditCardData);
            }
        } catch (JSONException e)
        {
            Log.e("Debug CTBC","ESUN_CreditCardLimit result = "+result);
            Log.e("Debug CTBC","ESUN_CreditCardLimit "+e.toString());
            e.printStackTrace();
        }
    }
}
