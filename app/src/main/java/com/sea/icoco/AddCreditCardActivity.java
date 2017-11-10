package com.sea.icoco;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;

public class AddCreditCardActivity extends AppCompatActivity
{
    private EditText cardNo_edt,cardCVV_edt,expMonth_edt,expYear_edt;
    private Button addCreditCard_btn;
    private DataControler dataControler = MainActivity.dataControler;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        findView();
        setOnClickListener();
    }

    private void setOnClickListener()
    {
        addCreditCard_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String cardNo = cardNo_edt.getText().toString();
                String cardCVV = cardCVV_edt.getText().toString();
                String expMonth = expMonth_edt.getText().toString();
                String expYear = expYear_edt.getText().toString();
                String expDate = expMonth+expYear;
                if(!cardNo.isEmpty() && !cardCVV.isEmpty() && !expDate.isEmpty())
                {
                    String sql = "INSERT INTO `icoco`.`usercreditcard` (`uid`, `cardNo`, `cardCVV`, `expDate`) " +
                            "VALUES ('"+dataControler.userData.getId()+"', '"+cardNo+"', '"+cardCVV+"', '"+expDate+"')";
                    new AddCreditCard().execute(sql);
                }

            }
        });
    }

    private void findView()
    {
        cardNo_edt = (EditText) findViewById(R.id.cardNo_edt);
        cardCVV_edt = (EditText) findViewById(R.id.cardCVV_edt);
        expMonth_edt = (EditText) findViewById(R.id.expMonth_edt);
        expYear_edt = (EditText) findViewById(R.id.expYear_edt);
        addCreditCard_btn = (Button) findViewById(R.id.addCreditCard_btn);
    }

    private class AddCreditCard extends MySQL_Execute
    {
        protected void onPostExecute(String result)
        {
            if (result.equals("true"))
            {
                new AlertDialog.Builder(AddCreditCardActivity.this).setTitle("信用卡新增訊息").setMessage("新增成功!\n\n即將跳回主選單").setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        AddCreditCardActivity.this.finish();
                    }
                }).setCancelable(false).show();
                dataControler.creditCardData.loadCreditData();
            }
            else
            {
                Log.e("Debug AddCreditCard","result = "+result);
            }
        }
    }
}
