package com.sea.icoco;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.sea.icoco.ActivitySupporter.ListAdapter_Transaction;
import com.sea.icoco.Control.DataControler;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchTransactionDetailActivity extends AppCompatActivity {
    DataControler dataControler = MainActivity.dataControler;
    ListView listView;
//    Button reload_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_transaction_detail);
        findView();
        setViewData();
        dataControler.payData.setAllRead();
//        setOnClickListener();
    }

//    private void setOnClickListener() {
//        reload_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                JSONObject data = new JSONObject();
////                try {
////                    data.put("context",SearchTransactionDetailActivity.this);
////                    data.put("listView",listView);
////                    dataControler.transactionData.loadTransactionData(data);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//            }
//        });
//    }

    private void setViewData()
    {
        listView.setAdapter(new ListAdapter_Transaction(this));
    }
    private void findView() {
        listView = (ListView) findViewById(R.id.listView);
//        reload_btn = (Button) findViewById(R.id.reload_btn);
    }
}
