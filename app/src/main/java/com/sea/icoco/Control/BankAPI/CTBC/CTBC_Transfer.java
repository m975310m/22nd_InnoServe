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

/**
 * Created by Administrator on 2016/9/6.
 */
public class CTBC_Transfer extends AsyncTask<String, Void, String>
{
    DataControler dataControler = MainActivity.dataControler;
    @Override
    protected String doInBackground(String... strings)
    {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection=null;
        try
        {
            String AccNo = strings[0];
            String Amt = strings[1];

            String link = "http://52.69.27.105:8080/hackathon/Transfer";
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
//            {
//                "CustID":"A923456789",
//                    "Token":"b0a9f129-d2c4-4d75-bfb4-f6c42dd68798",
//                    "PayerBankNO":"822",
//                    "PayerAcctNO":"0000999999999999",
//                    "PayeeBankNO":"822",
//                    "PayeeAcctNO":"0000999999999998",
//                    "TranAmt":"100",
//                    "Memo":"星聚點"
//            }
            jsonParam.put("CustID", dataControler.ctbcData.getCustID());
            jsonParam.put("Token", dataControler.ctbcData.getToken());
            jsonParam.put("PayerBankNO","822");
            jsonParam.put("PayerAcctNO",dataControler.ctbcData.getAc());
            jsonParam.put("PayeeBankNO","822");
            jsonParam.put("PayeeAcctNO",AccNo);
            jsonParam.put("TranAmt",Amt);
            jsonParam.put("Memo","iCoCo P2P 轉帳");

//            Log.d("debug","jsonParam= Save "+jsonParam.toString());
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

            Log.e("CTBC Transfer Error",e.toString());
        }
        catch (IOException e) {

            Log.e("CTBC Transfer Error",e.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.e("CTBC Transfer Error",e.toString());
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
            JSONObject CTBCTransferData = new JSONObject(result);
            if (CTBCTransferData.getString("ResponseCode").equals("0000"))
            {
                Log.d("Debug CTBC TransferData","CTBC Transfer Success !!");

//                dataControler.ctbcData.setBounsPoint(CTBCBounsPointData);
            }
        } catch (JSONException e)
        {
            Log.e("Debug CTBC TransferData","result = "+result);
            Log.e("Debug CTBC TransferData",e.toString());
            e.printStackTrace();
        }
    }
}
