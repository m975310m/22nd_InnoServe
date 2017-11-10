package com.sea.icoco.Entity;

import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Administrator on 2016/12/20.
 */

public class payData
{
    DataControler dataControler = MainActivity.dataControler;
    JSONArray payData;

    public JSONArray getPayData(){ return payData;}

    public JSONArray getPayDataOrderDesc()
    {
        JSONArray jsonArray = new JSONArray();
        for (int i = payData.length()-1 ; i > -1 ;i--)
        {
            try
            {
                jsonArray.put(payData.getJSONObject(i));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }


    public void loadPayData()
    {
        class SelectPayData extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    payData = new JSONArray(result);
                    Log.d("Debug Pay Data","Pay Data Load Success");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        String sql = "SELECT * FROM icoco.message_pay where `to` = "+dataControler.userData.getId()+";";
        new SelectPayData().execute(sql);
    }

    public Integer getNewPayDataLength()
    {
        Integer length = 0;
        for (int i = 0 ; i < payData.length() ; i++)
        {
            try
            {
                if (payData.getJSONObject(i).getString("read").equals("0"))
                    length++;
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return length;
    }

    public void setAllRead()
    {
        StringBuilder all_payid = new StringBuilder();
        for (int i = 0 ; i < payData.length() ; i++)
        {
            try
            {
                payData.getJSONObject(i).put("read","1");
                all_payid.append(payData.getJSONObject(i).getString("payid"));
                if (i < payData.length()-1)
                    all_payid.append(",");
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        String sql = "UPDATE `icoco`.`message_pay` SET `read`='1' WHERE `to`='"+dataControler.userData.getId()+"' and payid in ("+all_payid.toString()+");";
        new MySQL_Execute().execute(sql);
    }


}
