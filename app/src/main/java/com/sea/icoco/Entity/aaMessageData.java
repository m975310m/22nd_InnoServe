package com.sea.icoco.Entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/8.
 */
public class aaMessageData
{
    DataControler dataControler = MainActivity.dataControler;
    JSONArray aaMessage;

    public JSONArray getAAMessage(){ return aaMessage;}

    public JSONArray getAaMessageOrderDesc()
    {
        JSONArray jsonArray = new JSONArray();
        for (int i = aaMessage.length()-1 ; i > -1 ;i--)
        {
            try
            {
                jsonArray.put(aaMessage.getJSONObject(i));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }


    public void loadAAMessage()
    {
        class SelectAAMessage extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    aaMessage = new JSONArray(result);
                    Log.d("Debug AA Data","AA Message Data Load Success");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        String sql = "SELECT * FROM icoco.message_aa where `to` = "+dataControler.userData.getId()+";";
        new SelectAAMessage().execute(sql);
    }

    public Integer getNewMessageLength()
    {
        Integer length = 0;
        for (int i = 0 ; i < aaMessage.length() ; i++)
        {
            try
            {
                if (aaMessage.getJSONObject(i).getString("read").equals("0"))
                    length++;
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return length;
    }

    public void setAllRead()
    {
        StringBuilder all_messageid = new StringBuilder();
        for (int i = 0 ; i < aaMessage.length() ; i++)
        {
            try
            {
                aaMessage.getJSONObject(i).put("read","1");
                all_messageid.append(aaMessage.getJSONObject(i).getString("aaid"));
                if (i < aaMessage.length()-1)
                    all_messageid.append(",");
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        String sql = "UPDATE `icoco`.`message_aa` SET `read`='1' WHERE `to`='"+dataControler.userData.getId()+"' and aaid in ("+all_messageid.toString()+");";
        new MySQL_Execute().execute(sql);
    }


}
