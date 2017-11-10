package com.sea.icoco;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.sea.icoco.ActivitySupporter.ListAdapter;
import com.sea.icoco.ActivitySupporter.ListAdapter_QRCode;
import com.sea.icoco.ActivitySupporter.ListAdapter_SelectPoint;
import com.sea.icoco.Control.DataControler;

import org.json.JSONException;

public class ShopSummary extends AppCompatActivity {
    DataControler dataControler = MainActivity.dataControler;
    ImageView writeDiary_img;
    TextView shopName_txv,shopAddres_txv;
    String shopUid_str;
    Button coupon_btn;
    AlertDialog.Builder dialog,qrdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_summary);
        shopUid_str = this.getIntent().getStringExtra("shopUid");
        findView();
        setView();
        setClickEvent();
        onViewReady();
        dialogCreate();
    }

    private void dialogCreate() {
        ListAdapter_SelectPoint adapterItem = new ListAdapter_SelectPoint(ShopSummary.this);
        ListAdapter_QRCode qradapterItem = new ListAdapter_QRCode(ShopSummary.this);
        dialog = new AlertDialog.Builder(ShopSummary.this)
                .setTitle("選擇兌換點數")
                .setPositiveButton("確定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        qrdialog.show();
                    }
                })
                .setAdapter(adapterItem, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                    }
                });

        qrdialog = new AlertDialog.Builder(ShopSummary.this)
                .setTitle("請出示供商家掃描")
                .setPositiveButton("確定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setAdapter(qradapterItem, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_summary, menu);
        return true;
    }

    private void setView() {
        try {
            shopName_txv.setText(dataControler.shopData.getShopData_uid().getJSONObject(shopUid_str).getString("shop_name"));
            shopAddres_txv.setText(dataControler.shopData.getShopData_uid().getJSONObject(shopUid_str).getString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setClickEvent() {
        writeDiary_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopSummary.this,TakePictureActivity.class));
            }
        });

        coupon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    private void onViewReady() {
//        shopUid.setText(shopUid_str);
    }

    private void findView() {
        writeDiary_img = (ImageView) findViewById(R.id.writeDiary_img);
        shopName_txv = (TextView) findViewById(R.id.shopName_txv);
        shopAddres_txv = (TextView) findViewById(R.id.shopAddres_txv);
        coupon_btn = (Button) findViewById(R.id.coupon_btn);
    }
}
