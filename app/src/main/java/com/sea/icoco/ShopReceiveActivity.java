package com.sea.icoco;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea.icoco.ActivitySupporter.DownloadImageTask;
import com.sea.icoco.Control.BankAPI.CTBC.CTBC_Shop_CreditCardAuthorize;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopReceiveActivity extends AppCompatActivity
{
    DataControler dataControler = MainActivity.dataControler;
    JSONObject qrData;
    EditText receive_total_edt,receive_money_edt;
    TextView offAmt_txv;
    ImageView imageView;
    TextView name_txv;
    Button receuve_btn;
    boolean EmulationMode = dataControler.EmulationMode;
    boolean CTBCMode = dataControler.CTBCMode;
    boolean ESUNMode = dataControler.ESUNMode;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_receive);
        findView();
        getIntentData();
        setViewData();
        setKeyListener();
        setOnClickListener();
    }

    private void setKeyListener()
    {
        receive_total_edt.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                try
                {
                    Integer total = Integer.parseInt(receive_total_edt.getText().toString());
                    Integer offAmt = Integer.parseInt(offAmt_txv.getText().toString());
                    receive_money_edt.setText(String.valueOf(total-offAmt));
                }catch (Exception e) {}
                return false;
            }
        });
    }

    private void setOnClickListener()
    {
        receuve_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try{
                    final String receive_money = receive_money_edt.getText().toString();
                    String payerUid = qrData.getString("uid");
                    new AlertDialog.Builder(ShopReceiveActivity.this).setTitle("收款資訊").setMessage("信用卡授權成功\n\n即將返回選單").setNegativeButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ShopReceiveActivity.this.finish();
                        }
                    }).setCancelable(false).show();

//                    class SelectPayerData extends MySQL_Select
//                    {
//                        @Override
//                        protected void onPostExecute(String result) {
//                            Log.d("debug","result = "+result);
//                            if(!result.equals("[]"))
//                            {
//                                try {
//                                    JSONObject payerData = new JSONArray(result).getJSONObject(0);
//                                    String cardNo = payerData.getString("cardNo");
////                                                                      String expDate = payerData.getString("expDate");
//                                    String expDate = "1221";
//                                    String TransactionDescChinese = "iCoCo O2O付款";
//                                    class receive extends CTBC_Shop_CreditCardAuthorize
//                                    {
//                                        @Override
//                                        protected void onPostExecute(String result) {
//                                            try {
//                                                Log.d("debug",result.toString());
//                                                JSONObject recevieData = new JSONObject(result);
//
//                                                if (recevieData.getString("ResponseCode").equals("0000"))
//                                                {
//                                                    Log.d("Debug Shop","Receive Success !!");
//                                                    new AlertDialog.Builder(ShopReceiveActivity.this).setTitle("收款資訊").setMessage("信用卡授權成功\n\n即將返回選單").setNegativeButton("OK", new DialogInterface.OnClickListener()
//                                                    {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which)
//                                                        {
//                                                            ShopReceiveActivity.this.finish();
//                                                        }
//                                                    }).setCancelable(false).show();
//
//                                                }
//                                                else
//                                                    new AlertDialog.Builder(ShopReceiveActivity.this).setTitle("收款資訊").setMessage("信用卡授權失敗").setNegativeButton("OK",null).show();
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//
//                                    new receive().execute(cardNo,expDate,receive_money,TransactionDescChinese);
//
//                                } catch (JSONException e) {
//                                    Log.e("Debug","Receive Error"+e.toString());
//                                }
//                            }
//                            else
//                            {
//                                new AlertDialog.Builder(ShopReceiveActivity.this).setTitle("收款資訊").setMessage("對象尚未綁定信用卡").setNegativeButton("OK",null).show();
//                            }
//                        }
//                    }
                    String sql = "SELECT cardNo,expDate FROM icoco.usercreditcard where uid = "+payerUid+";";
//                    new SelectPayerData().execute(sql);
                    Log.d("debug","result = "+"start");

                }catch (Exception e) { Log.e("Debug","Receive Error"+e.toString()); }

            }
        });
    }

    private void setViewData()
    {
        try
        {
            new DownloadImageTask(imageView).execute("https://graph.facebook.com/"+qrData.getString("fbid")+"/picture?type=large");
            name_txv.setText(qrData.getString("name"));

            if(!qrData.getString("coupon").equals("-1"))
            {
//                Log.d("debug shop","myshop coupon"+dataControler.shopData.getMyShopCoupon_uid().toString());
                final JSONObject coupon = dataControler.shopData.getMyShopCoupon_uid().getJSONObject(qrData.getString("coupon"));
//                Log.d("debug coupon",coupon.toString());
                String message = "偵測到消費者使用消費劵 "+coupon.getString("off_amt")+" 折價劵\n\n是否使用？";
                new AlertDialog.Builder(ShopReceiveActivity.this).setTitle("優惠卷使用提醒").setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            offAmt_txv.setText(coupon.getString("off_amt"));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                }).setNegativeButton("NO",null).show();
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void getIntentData()
    {
        try
        {
            Intent intent = getIntent();
            qrData = new JSONObject(intent.getStringExtra("content"));
        } catch (JSONException e)
        { e.printStackTrace(); }
    }

    private void findView()
    {
        receive_total_edt = (EditText) findViewById(R.id.receive_total_edt);
        receive_money_edt = (EditText) findViewById(R.id.receive_money_edt);
        offAmt_txv = (TextView) findViewById(R.id.offAmt_txv);
        imageView = (ImageView) findViewById(R.id.imageView);
        name_txv = (TextView) findViewById(R.id.name_txv);
        receuve_btn = (Button) findViewById(R.id.receuve_btn);
    }
}
