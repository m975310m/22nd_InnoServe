package com.sea.icoco;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea.icoco.Control.AutoDetectionUpdate;
import com.sea.icoco.Control.DataControler;

public class MenuActivity extends AppCompatActivity
{
    DataControler dataControler = MainActivity.dataControler;
    private ImageView AA_img,transferBounsPoint_img,transferBalance_img,showQRCode_img,gopoint_img,serachMenu_img;
    Integer updateSecond = 1;
    ImageView redBall_img;
    TextView redBall_txv;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findView();
        setOnClickListener();
        new AutoDetectionUpdate();
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
        Integer newCouponLength = 0;
        Integer newAAMessageLength = 0;
        Integer newPayDataLength = 0;
        try{ newCouponLength = dataControler.couponData.getNewCouponLength();} catch (Exception e) {}
        try{ newAAMessageLength = dataControler.aaMessageData.getNewMessageLength(); } catch (Exception e) {}
        try{ newPayDataLength = dataControler.payData.getNewPayDataLength(); } catch (Exception e) {}
        Integer newSearchInt = newCouponLength+newAAMessageLength+newPayDataLength;

        if (newSearchInt > 0)
        {
            redBall_img.setVisibility(View.VISIBLE);
            redBall_txv.setVisibility(View.VISIBLE);
            redBall_txv.setText(String.valueOf(newSearchInt));
        }
        else
        {
            redBall_img.setVisibility(View.GONE);
            redBall_txv.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myshop:
                startActivity(new Intent(MenuActivity.this,ShopMenuActivity.class));
                return true;
            case R.id.action_addCreditCard:
                startActivity(new Intent(MenuActivity.this,AddCreditCardActivity.class));
                return true;
            case R.id.action_note:
                startActivity(new Intent(MenuActivity.this,TakePictureActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void setOnClickListener()
    {
        AA_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MenuActivity.this,AA_Activity.class));
            }
        });


        transferBounsPoint_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MenuActivity.this,MapsActivity.class));
            }
        });

        transferBalance_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MenuActivity.this,TransferBalanceActivity.class));
            }
        });

        showQRCode_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MenuActivity.this,QRCodeActivity.class));
            }
        });

        gopoint_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                startActivity(new Intent(MenuActivity.this,GoPointActivity.class)); //動畫用
                startActivity(new Intent(MenuActivity.this,RunpointActivity.class));
            }
        });

        serachMenu_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MenuActivity.this,SearchMenuActivity.class));
            }
        });
    }

    private void findView()
    {
        AA_img = (ImageView) findViewById(R.id.aa_img);
        transferBounsPoint_img  = (ImageView) findViewById(R.id.transferBounsPoint_img);
        transferBalance_img = (ImageView) findViewById(R.id.transferBalance_img);
        showQRCode_img = (ImageView) findViewById(R.id.showQRCode_img);
        gopoint_img = (ImageView) findViewById(R.id.gopoint_img);
        serachMenu_img = (ImageView) findViewById(R.id.serachMenu_img);
        redBall_img = (ImageView) findViewById(R.id.redBall_img);
        redBall_txv = (TextView) findViewById(R.id.redBall_txv);
    }



}
