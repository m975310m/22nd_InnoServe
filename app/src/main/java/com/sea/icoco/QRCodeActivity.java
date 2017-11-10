package com.sea.icoco;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea.icoco.ActivitySupporter.BuilderQRcode;
import com.sea.icoco.Control.DataControler;

import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeActivity extends AppCompatActivity
{
    ImageView qr_img;
    DataControler dataControler = MainActivity.dataControler;
    Calendar now;
    Handler handle_couponRebuild;
    TextView min_txv,sec_txv;
    int year,month,date,hour,min,sec;
    JSONObject qrJson;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        findView();

        qrJson = new JSONObject();
        String couponid = "-1";
        try
        {
            qrJson.put("uid",dataControler.userData.getId());
            qrJson.put("fbid",dataControler.userData.getFbid());
            qrJson.put("ac",dataControler.ctbcData.getAc());
            qrJson.put("bankNo","822");
            qrJson.put("name",dataControler.userData.getName());
            qrJson.put("coupon",couponid);
        } catch (Exception e) { Log.e("Debug QRCode",e.toString()); }

        String coupon_id = getIntent().getStringExtra("couponid");
        if (coupon_id != null)
        {
            try
            {
                qrJson.put("coupon", coupon_id);
                new BuilderQRcode(qr_img, qrJson.toString()).build();
            } catch (Exception e){}
        }
        else
        {
            if(dataControler.couponData.getCouponData().length()>0)
            {
                new AlertDialog.Builder(this).setTitle("提醒").setMessage("要使用優惠劵嗎").setPositiveButton("是", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        QRCodeActivity.this.finish();
                        startActivity(new Intent(QRCodeActivity.this,SearchCouponActivity.class));
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            new BuilderQRcode(qr_img,qrJson.toString()).build();
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                }).setCancelable(false).show();
            }
            else if (dataControler.couponData.getCouponData().length()<1)
            {
                try
                {
                    new BuilderQRcode(qr_img, qrJson.toString()).build();
                }catch (Exception e1) {};
            }
        }
        qrRebuildTimer();




    }


    private void qrRebuildTimer()
    {
        handle_couponRebuild = new Handler();
        final Runnable runnable = new Runnable()
        {
            int count = 0;
            public void run()
            {
                count++;
                if(count%(5*60)==0)
                {
                    reBuildQRCode();
                    sec_txv.setText("00");
                    min_txv.setText("5");
                }
                if(!((Integer.parseInt(sec_txv.getText().toString())-1)==-1))
                {
                    if((Integer.parseInt(sec_txv.getText().toString())-1) < 10)
                    {
                        sec_txv.setText("0"+String.valueOf(Integer.parseInt(sec_txv.getText().toString())-1));
                    }
                    else
                        sec_txv.setText(String.valueOf(Integer.parseInt(sec_txv.getText().toString())-1));
                }
                else
                {
                    if(!((Integer.parseInt(min_txv.getText().toString())-1)==-1))
                    {
                        sec_txv.setText("59");
                        min_txv.setText(String.valueOf(Integer.parseInt(min_txv.getText().toString())-1));
                    }
                    else
                    {
                        qrRebuildTimer();
                    }
                }
                Log.d("handler","QrcodeActivity.qrRebuildTimer="+count);
                handle_couponRebuild.postDelayed(this, 1*1000);
            }
        };
        handle_couponRebuild.post(runnable);
    }

    private void reBuildQRCode()
    {
        try
        {
            now = Calendar.getInstance(); //讀取現在時間
            loadTime();
            Long deadDatetime = year* 10000000000L+ month*100000000 + date*1000000 + hour*10000+min*500+sec*1;
            new BuilderQRcode(qr_img, qrJson.toString()).build();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    private void loadTime()
    {
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH)+1;
        date = now.get(Calendar.DATE);
        hour = now.get(Calendar.HOUR_OF_DAY);
        min = now.get(Calendar.MINUTE);
        sec = now.get(Calendar.SECOND);
    }



    private void findView()
    {
        qr_img = (ImageView) findViewById(R.id.qr_img);
        min_txv = (TextView) findViewById(R.id.min_txv);
        sec_txv = (TextView) findViewById(R.id.sec_txv);
    }
}
