package com.sea.icoco;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class ShopRegisterActivity extends AppCompatActivity
{
    DataControler dataControler = MainActivity.dataControler;
    Button getAddress_btn,regisger_btn;
    EditText shopName_edt,address_edt;
    TextView longitude_txv,latitude_txv;
    DecimalFormat df = new DecimalFormat("#.######");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register);
        findView();
        setOnClickListener();
    }

    private void setOnClickListener()
    {
        getAddress_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(dataControler.gpsData.getLocationLoadSuccess())
                {
                    address_edt.setText(dataControler.gpsData.getAddress());
                    longitude_txv.setText(df.format(dataControler.gpsData.getMyLocation().getLongitude()));
                    latitude_txv.setText(df.format(dataControler.gpsData.getMyLocation().getLatitude()));

                }
            }
        });

        regisger_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                class registerShop extends MySQL_Execute
                {
                    @Override
                    protected void onPostExecute(String result)
                    {
                        if(result.equals("true"))
                        {
                            new AlertDialog.Builder(ShopRegisterActivity.this).setTitle("註冊狀況").setMessage("商家註冊成功").setNegativeButton("OK",null).show();
                            dataControler.shopData.loadShopData();
                        }
                    }
                }

                if(dataControler.shopData.onLoadSuccess())
                {
                    try
                    {
                        JSONObject myShop = dataControler.shopData.getShopData_uid().getJSONObject(dataControler.userData.getId());
                        new AlertDialog.Builder(ShopRegisterActivity.this).setTitle("註冊狀況").setMessage("你已經是商家了").setNegativeButton("OK",null).show();
                    }
                    catch (Exception e)
                    {
                        String uid = dataControler.userData.getId();
                        String shop_name = (shopName_edt.getText().toString().isEmpty()) ? "":shopName_edt.getText().toString();
                        String address = (address_edt.getText().toString().isEmpty()) ? "":address_edt.getText().toString();
                        String longitude = (longitude_txv.getText().toString().isEmpty()) ? "":longitude_txv.getText().toString();
                        String latitude = (latitude_txv.getText().toString().isEmpty()) ? "":latitude_txv.getText().toString();

                        String sql = "INSERT INTO `icoco`.`shop` (`uid`, `shop_name`, `address`, `longitude`, `latitude`) VALUES ('"+uid+"', '"+shop_name+"', '"+address+"', '"+longitude+"', '"+latitude+"');";
                        new registerShop().execute(sql);
                    }
                }
            }
        });
    }

    private void findView()
    {
        getAddress_btn = (Button) findViewById(R.id.getAddress_btn);
        shopName_edt = (EditText) findViewById(R.id.shopName_edt);
        address_edt = (EditText) findViewById(R.id.address_edt);
        longitude_txv = (TextView) findViewById(R.id.longitude_txv);
        latitude_txv = (TextView) findViewById(R.id.latitude_txv);
        regisger_btn = (Button) findViewById(R.id.regisger_btn);
    }
}
