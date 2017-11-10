package com.sea.icoco;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;

public class SearchMenuActivity extends AppCompatActivity
{
    DataControler dataControler = MainActivity.dataControler;
    ImageView searchTransaction_img,searchCoupon_img,searchAA_img;
    public static ImageView redBall_img,redBall2_img,redBall3_img;
    public static TextView redBall_txv,redBall2_txv,redBall3_txv;
    Integer updateSecond = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);
        findView();
        setOnClickListener();
        autoUpdateRedBall();
    }

    private void autoUpdateRedBall()
    {
        final Handler handler_detectionUpdate = new Handler();
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                setRedBallData();
                handler_detectionUpdate.postDelayed(this, updateSecond*1000);
            }
        };
        handler_detectionUpdate.post(runnable);
    }

    private void setRedBallData()
    {
        Integer newCouponLength = dataControler.couponData.getNewCouponLength();
        if (newCouponLength > 0)
        {
            redBall_img.setVisibility(View.VISIBLE);
            redBall_txv.setVisibility(View.VISIBLE);
            redBall_txv.setText(String.valueOf(newCouponLength));
        }
        else
        {
            redBall_img.setVisibility(View.GONE);
            redBall_txv.setVisibility(View.GONE);
        }



        Integer newAAMessageLength = dataControler.aaMessageData.getNewMessageLength();
        if (newAAMessageLength > 0)
        {
            redBall2_img.setVisibility(View.VISIBLE);
            redBall2_txv.setVisibility(View.VISIBLE);
            redBall2_txv.setText(String.valueOf(newAAMessageLength));
        }
        else
        {
            redBall2_img.setVisibility(View.GONE);
            redBall2_txv.setVisibility(View.GONE);
        }

        Integer newPayDataLength = dataControler.payData.getNewPayDataLength();
        if (newPayDataLength > 0)
        {
            redBall3_img.setVisibility(View.VISIBLE);
            redBall3_txv.setVisibility(View.VISIBLE);
            redBall3_txv.setText(String.valueOf(newPayDataLength));
        }
        else
        {
            redBall3_img.setVisibility(View.GONE);
            redBall3_txv.setVisibility(View.GONE);
        }
    }

    private void setOnClickListener()
    {
        searchTransaction_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(SearchMenuActivity.this,SearchTransactionDetailActivity.class));
            }
        });

        searchCoupon_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(SearchMenuActivity.this,SearchCouponActivity.class));
            }
        });

        searchAA_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(SearchMenuActivity.this,SearchAAActivity.class));
            }
        });
    }

    private void findView()
    {
        searchTransaction_img = (ImageView) findViewById(R.id.searchTransaction_img);
        searchCoupon_img = (ImageView) findViewById(R.id.searchCoupon_img);
        searchAA_img = (ImageView) findViewById(R.id.searchAA_img);

        redBall_img = (ImageView) findViewById(R.id.redBall_img);
        redBall_txv = (TextView) findViewById(R.id.redBall_txv);
        redBall2_img = (ImageView) findViewById(R.id.redBall2_img);
        redBall2_txv = (TextView) findViewById(R.id.redBall2_txv);
        redBall3_img = (ImageView) findViewById(R.id.redBall3_img);
        redBall3_txv = (TextView) findViewById(R.id.redBall3_txv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRedBallData();
    }
}
