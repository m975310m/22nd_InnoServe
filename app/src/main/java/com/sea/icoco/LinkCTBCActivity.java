package com.sea.icoco;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sea.icoco.Control.BankAPI.CTBC.CTBC_Login;
import com.sea.icoco.Control.BankAPI.Emulation.Login;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;

import org.json.JSONException;
import org.json.JSONObject;

public class LinkCTBCActivity extends AppCompatActivity
{
    private EditText custID_edt,userID_edt,pin_edt;
    private Button link_btn;
    private DataControler dataControler = MainActivity.dataControler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_ctbc);
        findView();
        setOnClickListener();
    }

    private void setOnClickListener()
    {
        link_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (dataControler.ESUNMode){
                    if (!custID_edt.getText().toString().isEmpty() && !userID_edt.getText().toString().isEmpty() && !pin_edt.getText().toString().isEmpty())
                    {
                        new myCTBCLogin().execute(custID_edt.getText().toString(),userID_edt.getText().toString(),pin_edt.getText().toString());
                    }
                }else{
                    if (!custID_edt.getText().toString().isEmpty() && !userID_edt.getText().toString().isEmpty() && !pin_edt.getText().toString().isEmpty())
                        new myLogin().execute(custID_edt.getText().toString(),dataControler.userData.getId(),pin_edt.getText().toString());
                }
            }
        });
    }

    private void findView()
    {
        custID_edt = (EditText) findViewById(R.id.custID_edt);
        userID_edt = (EditText) findViewById(R.id.userID_edt);
        pin_edt = (EditText) findViewById(R.id.pin_edt);
        link_btn = (Button) findViewById(R.id.link_btn);
    }

    private class myLogin extends Login
    {
        protected void onPostExecute(String result)
        {
            if (!result.equals(""))  //檢查PHP是否為空　如果不為空
            {
                try
                {
//                    Log.d("debug test",result);
                    JSONObject LoginData = new JSONObject(result);
                    if (!LoginData.getString("Token").isEmpty())
                    {
                        String sql = "UPDATE `icoco`.`user` SET `CustID`='" + CustID + "', `ctbcAc`='" + UserID + "', `ctbcPin`='" + PIN + "' WHERE `uid`='" + dataControler.userData.getId() + "'";
                        dataControler.userData.setCustID(custID_edt.getText().toString());
                        new CTBC_Link().execute(sql);
                    }
                    dataControler.ctbcData.setToken(LoginData);
                } catch (JSONException e)
                {
                    Log.e("Debug LoginData",e.toString());
                }
            } else
            {
                Log.e("Debug My Login", "No Reuslt");
            }
        }
    }

    private class myCTBCLogin extends CTBC_Login
    {
        protected void onPostExecute(String result)
        {
            if(!result.equals(""))  //檢查PHP是否為空　如果不為空
            {
                try
                {
                    JSONObject CTBCLoginData = new JSONObject(result);
                    if (!CTBCLoginData.getString("Token").isEmpty())
                    {
                        String sql = "UPDATE `icoco`.`user` SET `CustID`='"+CustID+"', `ctbcAc`='"+UserID+"', `ctbcPin`='"+PIN+"' WHERE `uid`='"+dataControler.userData.getId()+"'";
                        dataControler.userData.setCustID(custID_edt.getText().toString());
                        new CTBC_Link().execute(sql);
                    }
                    dataControler.ctbcData.setToken(CTBCLoginData);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.e("Debug My CTBC Login","No Reuslt");
            }
        }
    }

    private class CTBC_Link extends MySQL_Execute
    {
        protected void onPostExecute(String result)
        {
            if(result.equals("true"))  //如果回傳成功
            {
                Log.d("Debug SetCreditCard","Set CreditCard Success");
                new AlertDialog.Builder(LinkCTBCActivity.this).setTitle("連結帳號訊息").setMessage("綁定成功!\n\n即將跳回主選單").setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        LinkCTBCActivity.this.finish();
                        startActivity(new Intent(LinkCTBCActivity.this,MenuActivity.class));
                    }
                }).setCancelable(false).show();
            }
        }
    }
}

