package com.sea.icoco;

import android.content.DialogInterface;
import android.graphics.Bitmap;
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

import com.sea.icoco.ActivitySupporter.ListAdapter;
import com.sea.icoco.Control.BankAPI.CTBC.CTBC_F_TransferBouns;
import com.sea.icoco.Control.DataControler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransferBounsPointActivity extends AppCompatActivity
{
    DataControler dataControler = MainActivity.dataControler;
    TextView friendName_txv;
    ImageView friendPicture_img;
    Button transferBouns_btn;
    AlertDialog.Builder dialog;
    EditText bounsPoint_edt,traAmt_edt,leftBouns_edt;
    int choose = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_bouns_point);
        findView();
        setViewInitData();
        setOnClickListener();
        setOnKeyListener();
        friendsDialogCreate();
    }

    private void setOnKeyListener()
    {
        traAmt_edt.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent)
            {
                try
                {
                    leftBouns_edt.setText(String.valueOf(Double.parseDouble(bounsPoint_edt.getText().toString()) - Double.parseDouble(traAmt_edt.getText().toString())));
                }
                catch (Exception e)
                {
                    Log.e("debug",e.toString());
                }
                return false;
            }
        });
    }

    private void friendsDialogCreate()
    {
        final ArrayList<String> friendsData = new ArrayList<String>();
        //JSONArray 轉 ArrayList
        try
        {
            for (int j=0; j < dataControler.friendsData.getFriendsData().length(); j++)
                friendsData.add(dataControler.friendsData.getFriendsData().getJSONObject(j).getString("name"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        ListAdapter adapterItem = new ListAdapter(TransferBounsPointActivity.this, friendsData);

        dialog = new AlertDialog.Builder(TransferBounsPointActivity.this)
                .setTitle("請選擇好友")
                .setAdapter(adapterItem, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        try
                        {
                            friendPicture_img.setImageBitmap((Bitmap) dataControler.friendsData.getFriendsData().getJSONObject(i).get("picture"));
                            friendName_txv.setText(dataControler.friendsData.getFriendsData().getJSONObject(i).getString("name"));
                            choose = i;
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setOnClickListener()
    {
        friendPicture_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.show();
            }
        });

        transferBouns_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!traAmt_edt.getText().toString().isEmpty() && dataControler.ctbcData.isLoginSuccess())
                {
                    try
                    {
                        String Token = dataControler.ctbcData.getToken();
                        String receiveID = dataControler.friendsData.getFriendsData().getJSONObject(choose).getString("uid");
                        String TranAmt = traAmt_edt.getText().toString();
                        new TransferBounsPoint().execute(Token,receiveID,TranAmt);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setViewInitData()
    {
        if (dataControler.ctbcData.isLoginSuccess())
        {
            bounsPoint_edt.setText(String.valueOf(dataControler.ctbcData.getBounsPoint()));
        }
        else
            bounsPoint_edt.setText("請先綁定 中國信託 帳戶");
    }

    private void findView()
    {
        friendName_txv = (TextView) findViewById(R.id.friendName_txv);
        friendPicture_img = (ImageView) findViewById(R.id.friendPicture_img);
        transferBouns_btn = (Button) findViewById(R.id.transferBouns_btn);
        bounsPoint_edt = (EditText) findViewById(R.id.bounsPoint_edt);
        traAmt_edt = (EditText) findViewById(R.id.traAmt_edt);
        leftBouns_edt = (EditText) findViewById(R.id.leftBouns_edt);
    }

    private class TransferBounsPoint extends CTBC_F_TransferBouns
    {
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            try
            {
                if (new JSONObject(result).getString("RspCode").equals("0000"))
                {
                    new AlertDialog.Builder(TransferBounsPointActivity.this).setTitle("轉讓紅利資訊").setMessage("轉讓紅利成功!!").setPositiveButton("OK",null).show();
                    bounsPoint_edt.setText(String.valueOf(dataControler.ctbcData.getBounsPoint()));
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            leftBouns_edt.setText(String.valueOf(Double.parseDouble(bounsPoint_edt.getText().toString()) - Double.parseDouble(traAmt_edt.getText().toString())));
        }
    }
}
