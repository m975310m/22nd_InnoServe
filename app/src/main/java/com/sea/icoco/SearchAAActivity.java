package com.sea.icoco;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sea.icoco.ActivitySupporter.ListAdapter_Message;
import com.sea.icoco.Control.DataControler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchAAActivity extends AppCompatActivity
{
    DataControler dataControler = MainActivity.dataControler;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_aa);
        findView();
        setViewData();
        setOnClickListener();
        dataControler.aaMessageData.setAllRead();
//        dataControler.myDBHelper.getWritableDatabase().update()
//        Log.d("debug Cusor",data.toString());
    }

    private void setOnClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                try
                {
                    JSONObject data = new JSONObject();
                    String uid = dataControler.aaMessageData.getAaMessageOrderDesc().getJSONObject(position).getString("from");
                    data.put("money",dataControler.aaMessageData.getAaMessageOrderDesc().getJSONObject(position).getString("money"));
                    data.put("name",dataControler.friendsData.getFriendsDataUID().getJSONObject(uid).getString("name"));
                    data.put("fbid",dataControler.friendsData.getFriendsDataUID().getJSONObject(uid).getString("id"));
                    Integer friendPosition = -1;
                    for (int i = 0 ; i < dataControler.friendsData.getFriendsData().length() ; i++)
                    {
                        JSONObject friend = dataControler.friendsData.getFriendsData().getJSONObject(i);
                        if (friend.getString("uid").equals(uid))
                        {
                            friendPosition = i;
                            break;
                        }
                    }
                    data.put("position",friendPosition);
                    startActivity(new Intent(SearchAAActivity.this,TransferBalanceActivity.class).putExtra("AA",data.toString()));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void findView()
    {
        listView = (ListView) findViewById(R.id.listView);
    }

    private void setViewData()
    {
        listView.setAdapter(new ListAdapter_Message(this,dataControler.aaMessageData.getNewMessageLength()));
    }

}
