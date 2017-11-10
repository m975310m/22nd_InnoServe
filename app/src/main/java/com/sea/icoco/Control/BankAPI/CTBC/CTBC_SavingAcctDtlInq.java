package com.sea.icoco.Control.BankAPI.CTBC;

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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/6.
 */
public class CTBC_SavingAcctDtlInq extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat yearMonthFormatter = new SimpleDateFormat("yyyy-MM");
    @Override
    protected String doInBackground(String... strings)
    {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection=null;
        try
        {
            String link = "http://52.69.27.105:8080/hackathon/SavingAcctDtlInq";
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
//            urlConnection.connect();
//            {
//                "CustID":"A923456789",
//                    "AcctNO":"0000999999999999",
//                    "Token":"b0a9f129-d2c4-4d75-bfb4-f6c42dd68798",
//                    "InqStartDt":"2016-01 -01",
//                    "InqDueDt":"2016-01 -31"
//            }
            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("CustID", dataControler.esunData.getCustID());
            jsonParam.put("Token", dataControler.esunData.getToken());
            jsonParam.put("AcctNO",dataControler.esunData.getAccData().getJSONObject(0).getString("account_id"));
//            jsonParam.put("InqStartDt","2016-01-01");
//            jsonParam.put("InqDueDt","2016-12-31");

            jsonParam.put("InqStartDt",yearMonthFormatter.format(new Date())+"-01");
            jsonParam.put("InqDueDt",yearMonthFormatter.format(new Date())+"-31");

//            Log.d("debug","jsonParam Detail= "+jsonParam.toString());
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
            JSONObject CTBCBalanceData = new JSONObject(result);
            if (CTBCBalanceData.getString("ResponseCode").equals("0000"))
            {
//                Log.d("Debug CTBC BounsPoint","Get CTBC Bouns Point Data Success !!");
//                dataControler.ctbcData.setBalance(CTBCBalanceData);
            }
        } catch (JSONException e)
        {
            Log.e("Debug CTBC","ESUN_BonusPointInq result = "+result);
            Log.e("Debug CTBC","ESUN_BonusPointInq"+e.toString());
            e.printStackTrace();
        }
    }
}
