package com.sea.icoco;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.sea.icoco.ActivitySupporter.DownloadImageTask;
import com.sea.icoco.ActivitySupporter.ListAdapter;
import com.sea.icoco.Control.Action.ScanerQRCode;
import com.sea.icoco.Control.BankAPI.CTBC.CTBC_SavingAcctInq;
import com.sea.icoco.Control.BankAPI.CTBC.CTBC_Transfer;
import com.sea.icoco.Control.BankAPI.ESUN.Bank.ESUN_Transfer;
import com.sea.icoco.Control.BankAPI.Emulation.SavingAcctInq;
import com.sea.icoco.Control.BankAPI.Emulation.Transfer;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransferBalanceActivity extends AppCompatActivity
{
    EditText balance_edt,leftBalance_edt,traAmt_edt;
    DataControler dataControler = MainActivity.dataControler;
    AlertDialog.Builder dialog;
    ImageView friendPicture_img;
    TextView friendName_txv;
    Button transferBalance_btn;
    int choose = -1;
    boolean fromQRCode = false;
    JSONObject receiveQRData,receiveAAData;

    boolean EmulationMode = dataControler.EmulationMode;
    boolean CTBCMode = dataControler.CTBCMode;
    boolean ESUNMode = dataControler.ESUNMode;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_balance);
        findView();
        setViewData();
        setOnKeyListener();
        friendsDialogCreate();
        setOnClickListener();
        setIfQRCode();
        setIfAAMessage();
    }

    private void setIfAAMessage()
    {
        try
        {
            Intent intent = getIntent();
            receiveAAData = new JSONObject(intent.getStringExtra("AA"));
            Log.d("debug aa data",receiveAAData.toString());
            new DownloadImageTask(friendPicture_img).execute("https://graph.facebook.com/"+receiveAAData.getString("fbid")+"/picture?type=large");
            friendName_txv.setText(receiveAAData.getString("name"));
            choose = Integer.parseInt(receiveAAData.getString("position"));
            traAmt_edt.setText(receiveAAData.getString("money"));
            leftBalance_edt.setText(String.valueOf((dataControler.ctbcData.getBalance() - Double.parseDouble(receiveAAData.getString("money")))));
        } catch (Exception e) { Log.d("Debug Get Intent","No Data in Intent"); }
    }

    private void setIfQRCode()
    {
        try
        {
            Intent intent = getIntent();
//            qrJson.put("uid",dataControler.userData.getId());
//            qrJson.put("fbid",dataControler.userData.getFbid());
//            qrJson.put("ac",dataControler.ctbcData.getAc());
//            qrJson.put("bankNo","822");
//            qrJson.put("name",dataControler.userData.getName());

            receiveQRData = new JSONObject(intent.getStringExtra("content"));
            new DownloadImageTask(friendPicture_img).execute("https://graph.facebook.com/"+receiveQRData.getString("fbid")+"/picture?type=large");
            friendName_txv.setText(receiveQRData.getString("name"));
            fromQRCode = true;
        } catch (Exception e) { Log.d("Debug Get Intent","No Data in Intent"); }
    }

    private void setViewData()
    {
        if (ESUNMode){
            balance_edt.setText(String.valueOf(dataControler.esunData.getBalance()));
        }
        else if (CTBCMode){
            balance_edt.setText(String.valueOf(dataControler.ctbcData.getBalance()));
        }
        else if (EmulationMode){
            balance_edt.setText(String.valueOf(dataControler.ctbcData.getBalance()));
        }

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
                    leftBalance_edt.setText(String.valueOf(Double.parseDouble(balance_edt.getText().toString()) - Double.parseDouble(traAmt_edt.getText().toString())));
                }
                catch (Exception e)
                {
                    Log.e("debug",e.toString());
                }
                return false;
            }
        });
    }

    private void findView()
    {
        balance_edt = (EditText) findViewById(R.id.balance_edt);
        leftBalance_edt = (EditText) findViewById(R.id.leftBalance_edt);
        traAmt_edt = (EditText) findViewById(R.id.traAmt_edt);
        friendPicture_img = (ImageView) findViewById(R.id.friendPicture_img);
        friendName_txv = (TextView) findViewById(R.id.friendName_txv);
        transferBalance_btn = (Button) findViewById(R.id.transferBalance_btn);
    }

    private void friendsDialogCreate()
    {
        final ArrayList<String> friendsData = new ArrayList<String>();
        //JSONArray 轉 ArrayList
        try
        {
            for (int j=0; j < dataControler.friendsData.getFriendsData().length(); j++)
                friendsData.add(dataControler.friendsData.getFriendsData().getJSONObject(j).getString("name"));
        } catch (JSONException e) { e.printStackTrace(); }

        ListAdapter adapterItem = new ListAdapter(TransferBalanceActivity.this, friendsData);

        dialog = new AlertDialog.Builder(TransferBalanceActivity.this)
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
                }).setNegativeButton("QR Code 付款", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        new IntentIntegrator(TransferBalanceActivity.this).setOrientationLocked(false).setCaptureActivity(ScannerActivity.class).initiateScan();
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

        transferBalance_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!traAmt_edt.getText().toString().isEmpty() && dataControler.friendsData.isLoadCTBCAcSuccess())
                {
                    try
                    {
                        String TranAmt = traAmt_edt.getText().toString();

                        class myCTBC_Transfer extends CTBC_Transfer
                        {
                            @Override
                            protected void onPostExecute(String result)
                            {
                                super.onPostExecute(result);
                                class myCTBC_CTBC_SavingAcctInq extends CTBC_SavingAcctInq
                                {
                                    @Override
                                    protected void onPostExecute(String result)
                                    {
                                        super.onPostExecute(result);
                                        setViewData();
                                        new AlertDialog.Builder(TransferBalanceActivity.this).setTitle("轉帳資訊").setMessage("轉帳成功").show();
                                        leftBalance_edt.setText(String.valueOf(Double.parseDouble(balance_edt.getText().toString()) - Double.parseDouble(traAmt_edt.getText().toString())));
                                    }
                                }


                                new myCTBC_CTBC_SavingAcctInq().execute();
                            }
                        }

                        class myTransfer extends Transfer
                        {
                            @Override
                            protected void onPostExecute(String result)
                            {
                                super.onPostExecute(result);
                                class mySavingAcctInq extends SavingAcctInq
                                {
                                    @Override
                                    protected void onPostExecute(String result)
                                    {
                                        super.onPostExecute(result);
                                        setViewData();
                                        new AlertDialog.Builder(TransferBalanceActivity.this).setTitle("轉帳資訊").setMessage("轉帳成功").show();
                                        leftBalance_edt.setText(String.valueOf(Double.parseDouble(balance_edt.getText().toString()) - Double.parseDouble(traAmt_edt.getText().toString())));
                                    }
                                }

                                new mySavingAcctInq().execute();
                            }
                        }

                        class myESUNTransfer extends ESUN_Transfer
                        {
                            @Override
                            protected void onPostExecute(String result)
                            {
                                super.onPostExecute(result);
                                class mySavingAcctInq extends SavingAcctInq
                                {
                                    @Override
                                    protected void onPostExecute(String result)
                                    {
                                        super.onPostExecute(result);
                                        setViewData();
                                        new AlertDialog.Builder(TransferBalanceActivity.this).setTitle("轉帳資訊").setMessage("轉帳成功").show();
                                        leftBalance_edt.setText(String.valueOf(Double.parseDouble(balance_edt.getText().toString()) - Double.parseDouble(traAmt_edt.getText().toString())));
                                    }
                                }
                                new mySavingAcctInq().execute();
                            }
                        }

                        if (fromQRCode)
                        {
                            String receiveID = receiveQRData.getString("uid");
                            String AccNo = receiveQRData.getString("ac");
                            if (EmulationMode)
                            {
                                new myTransfer().execute(receiveID,TranAmt);
                            }
                            else if (CTBCMode)
                            {
                                new myCTBC_Transfer().execute(AccNo,TranAmt);
                            }
                            else if (ESUNMode){
                                new myESUNTransfer().execute(AccNo,TranAmt);
                            }


                            String sql = "UPDATE `icoco`.`userupdate` SET `balance`='1',`pay`='1' WHERE `uid`='"+receiveID+"';";
                            sql += "INSERT INTO `icoco`.`message_pay` (`from`, `to`, `money`, `read`) VALUES ('"+dataControler.userData.getId()+"', '"+receiveID+"', '"+TranAmt+"', '0');";
                            new MySQL_Execute().execute(sql);
                        }
                        else if (!fromQRCode)
                        {
                            Log.d("debug choose", String.valueOf(choose));
                            if (dataControler.friendsData.getFriendsData().getJSONObject(choose).getJSONArray("bank").length() > 0)
                            {
                                String receiveID = dataControler.friendsData.getFriendsData().getJSONObject(choose).getString("uid");
                                String AccNo = dataControler.friendsData.getFriendsDataUID().getJSONObject(receiveID).getJSONArray("bank").getString(0);
                                Log.d("debug","myCTBC_Transfer"+receiveID+","+AccNo);


                                if (EmulationMode)
                                {
                                    new myTransfer().execute(receiveID,TranAmt);
                                }
                                else if (CTBCMode) {
                                    new myCTBC_Transfer().execute(AccNo,TranAmt);
                                }
                                else if (ESUNMode){
                                    new myESUNTransfer().execute(AccNo,TranAmt);
                                }
                                String sql = "UPDATE `icoco`.`userupdate` SET `balance`='1',`pay`='1' WHERE `uid`='"+receiveID+"';";
                                sql += "INSERT INTO `icoco`.`message_pay` (`from`, `to`, `money`, `read`) VALUES ('"+dataControler.userData.getId()+"', '"+receiveID+"', '"+TranAmt+"', '0');";
                                new MySQL_Execute().execute(sql);
                            }
                            else
                                {new AlertDialog.Builder(TransferBalanceActivity.this).setTitle("轉帳結果").setMessage("轉帳成功").setNegativeButton("OK",null).show();}
                        }
                    } catch (JSONException e) { Log.e("Debug transferError",e.toString());}
                }
            }
        });
    }


    //回傳結果 適當修改
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        //付款
        //設定進入的介面
        if (requestCode == 49374 && resultCode == -1)
        {
            ScanerQRCode scanerQRCode = new ScanerQRCode(requestCode,resultCode,intent,this,TransferBalanceActivity.class);
            scanerQRCode.getContextResult();
        }

    }
}
