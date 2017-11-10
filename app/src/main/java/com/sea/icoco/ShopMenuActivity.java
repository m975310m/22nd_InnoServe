package com.sea.icoco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.sea.icoco.Control.Action.ScanerQRCode;

public class ShopMenuActivity extends AppCompatActivity
{
    Button register_btn,receive_btn,couponInsert_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);
        findView();
        setOnClickListener();
    }

    private void setOnClickListener()
    {
        register_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(ShopMenuActivity.this,ShopRegisterActivity.class));
            }
        });

        receive_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new IntentIntegrator(ShopMenuActivity.this).setOrientationLocked(false).setCaptureActivity(ScannerActivity.class).initiateScan();
            }
        });

        couponInsert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopMenuActivity.this,ShopInsertCouponActivity.class));
            }
        });
    }

    private void findView()
    {
        register_btn = (Button) findViewById(R.id.register_btn);
        receive_btn = (Button) findViewById(R.id.receive_btn);
        couponInsert_btn = (Button) findViewById(R.id.couponInsert_btn);
    }

    //回傳結果 適當修改
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        //付款
        //設定進入的介面
        if (requestCode == 49374 && resultCode == -1)
        {
            ScanerQRCode scanerQRCode = new ScanerQRCode(requestCode,resultCode,intent,this,ShopReceiveActivity.class);
            scanerQRCode.getContextResult();
        }
    }
}
