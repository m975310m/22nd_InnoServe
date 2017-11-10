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
public class ESUN_Transfer extends AsyncTask<String, Void, String>
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

            String link = "https://eospu.esunbank.com.tw/esun/bank/public/v1/user/outbank_transfer?client_id="+dataControler.esunData.client_id;
//            String data = URLEncoder.encode("Token", "UTF-8") + "=" + URLEncoder.encode(Token, "UTF-8");
            URL url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
//            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setRequestProperty("Authorization","Bearer "+dataControler.esunData.getToken());
            urlConnection.connect();

            //Create JSONObject here
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
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("outward_account", dataControler.esunData.getAc());
            jsonParam.put("inward_account", AccNo);
            jsonParam.put("transfer_amount", Amt);
            jsonParam.put("id", dataControler.esunData.getCustID());

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

            Log.e("ESUN Transfer Error",e.toString());
        }
        catch (IOException e) {

            Log.e("ESUN Transfer Error",e.toString());
        } catch (JSONException e) {
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
            JSONObject CTBCTransferData = new JSONObject(result);
            if (!CTBCTransferData.getString("transaction_id").isEmpty())
            {
                Log.d("Debug ESUN TransferData","ESUN Transfer Success !!");
                new ESUN_SavingAcctInq().execute(dataControler.esunData.getToken());
//                dataControler.ctbcData.setBounsPoint(CTBCBounsPointData);
            }
        } catch (JSONException e)
        {
            Log.e("Debug ESUN TransferData","result = "+result);
            Log.e("Debug ESUN TransferData",e.toString());
            e.printStackTrace();
        }
    }
}
