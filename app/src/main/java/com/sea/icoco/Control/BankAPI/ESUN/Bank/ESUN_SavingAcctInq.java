package com.sea.icoco.Control.BankAPI.ESUN.Bank;

import android.os.AsyncTask;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;

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

/**
 * Created by Administrator on 2016/9/6.
 */
public class ESUN_SavingAcctInq extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    @Override
    protected String doInBackground(String... strings)
    {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection=null;
        try
        {
            String link = "https://eospu.esunbank.com.tw/esun/bank/public/v1/user/" +
                    dataControler.userData.getCustID() +
                    "/accounts" +
                    "?" + URLEncoder.encode("birthday", "UTF-8") + "=" + URLEncoder.encode("770211", "UTF-8")+
                    "&" + URLEncoder.encode("client_id", "UTF-8") + "=" + URLEncoder.encode(dataControler.esunData.client_id, "UTF-8");
            URL url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization","Bearer "+dataControler.esunData.getToken());;
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();

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
        try
        {
//            Log.d("Debug ESUN BalanceData","result = "+result);
            JSONObject ESUNBalanceData = new JSONObject(result);
            if (ESUNBalanceData.getJSONArray("details").length()>0)
            {
                dataControler.esunData.setBalance(ESUNBalanceData);
//                dataControler.transactionData.loadTransactionData();
            }
        } catch (JSONException e)
        {
            Log.e("Debug ESUN","ESUN_SavingAcctInq result = "+result);
            Log.e("Debug ESUN","ESUN_SavingAcctInq "+e.toString());
            e.printStackTrace();
        }
    }
}
