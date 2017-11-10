package com.sea.icoco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sea.icoco.ActivitySupporter.ListAdapter_Coupon;
import com.sea.icoco.Control.DataControler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchCouponActivity extends AppCompatActivity
{
    ListView listView;
    DataControler dataControler = MainActivity.dataControler;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_coupon);
        findView();
        setViewData();
        setOnClickListener();
        dataControler.couponData.setAllRead();
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
                    JSONObject coupon = dataControler.couponData.getCouponData().getJSONObject(position);
                    Intent intent = new Intent(SearchCouponActivity.this,QRCodeActivity.class).putExtra("couponid",coupon.getString("coupon_id"));
                    startActivity(intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setViewData()
    {
        listView.setAdapter(new ListAdapter_Coupon(this,dataControler.couponData.getNewCouponLength()));
    }

    private void findView()
    {
        listView = (ListView) findViewById(R.id.listView);
    }
}
