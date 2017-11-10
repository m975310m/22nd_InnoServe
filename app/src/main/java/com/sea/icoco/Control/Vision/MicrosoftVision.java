package com.sea.icoco.Control.Vision;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;
import com.sea.icoco.TakePictureActivity;

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
import java.net.URLEncoder;

/**
 * Created by AndyChuo on 2016/4/12.
 */
public class MicrosoftVision extends AsyncTask<String, Void, String>
{

    DataControler dataControler = MainActivity.dataControler;
    //flag 0 means get and 1 means post.(By default it is get.)
    Context context = null;
    public MicrosoftVision(Context context){
        this.context = context;
    }
    protected void onPreExecute()
    {}

    protected String doInBackground(String... arg)
    {
        String fileName = arg[0];

        StringBuilder sb = new StringBuilder();

        String link = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr?language=en&detectOrientation=true";


        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key","1f400ac5d22e4ae592d0a02e93c586d2");
            urlConnection.connect();

            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("url", "http://"+dataControler.server+"/iCoCo/uploads/"+fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }


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
        } finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(!result.isEmpty())  //檢查是否為空　如果不為空
        {
            try
            {
                String text = "";
                JSONArray regions = new JSONObject(result).getJSONArray("regions");
                for (int i = 0 ; i < regions.length() ; i ++){
                    JSONArray lines = regions.getJSONObject(i).getJSONArray("lines");
                    for (int j = 0 ; j < lines.length() ; j++){
                        JSONArray words = lines.getJSONObject(i).getJSONArray("words");
                        for (int k = 0 ; k < words.length() ; k++){
                            text+=words.getJSONObject(k).getString("text");
                        }
                    }
                }
                Log.d("Debug Vision","result = "+text);
                String type = "null";
                if (text.length() > 10){
                    type = "菜單";
                }else {
                    type = "美食";
                }

                Toast.makeText(context, "類別辨識 = "+type, Toast.LENGTH_SHORT).show();


            } catch (JSONException e)
            {
                Log.e("Debug Vision",result);
                Log.e("Debug Vision",e.toString());
                e.printStackTrace();
            }
        }
    }
}
