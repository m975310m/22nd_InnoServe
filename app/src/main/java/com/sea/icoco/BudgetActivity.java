package com.sea.icoco;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sea.icoco.Control.Action.AutoDetectionCoupon_Budget;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;

import java.text.DecimalFormat;

public class BudgetActivity extends AppCompatActivity
{
    TextView percent_txv;
    DataControler dataControler = MainActivity.dataControler;
    EditText budget_edt,total_expense_edt,expenseAmt_edt;
    AlertDialog setBudgetDialog;
    View set_budget_view;
    ProgressBar progressBar;
    Spinner spinner;
    DecimalFormat df = new DecimalFormat("#.##");
    String[] chooseType = new String[]{"飲食","娛樂"};
    String[] chooseType_en = new String[]{"eat","fun"};
    String selectType = "";
    Button expenseAdd_btn,getCoupon_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        findView();
        setViewData();
        createDialog();
        setOnClickListener();
    }

    private void setViewData()
    {
        budget_edt.setText(String.valueOf(dataControler.budgetData.getBudget()));
        total_expense_edt.setText(String.valueOf(dataControler.budgetData.getTotal_expense()));
        progressBar.setProgress(dataControler.budgetData.getScale().intValue());
        percent_txv.setText(String.valueOf(dataControler.budgetData.getScale().intValue())+" %");

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,chooseType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void createDialog()
    {
        setBudgetDialog = new AlertDialog.Builder(BudgetActivity.this)
                .setTitle("設定新預算")
                .setView(set_budget_view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        final EditText setbudget_edt = (EditText) (set_budget_view.findViewById(R.id.setBudget_edt));
                        class updateBudget extends MySQL_Execute
                        {
                            @Override
                            protected void onPostExecute(String result)
                            {
                                if(result.equals("true"))
                                {
//                                    dataControler.budgetData.loadBudgetData();
                                    Double newBudget = Double.parseDouble(setbudget_edt.getText().toString());
                                    Double scale = Double.parseDouble(df.format( (dataControler.budgetData.getTotal_expense() / newBudget)*100));
                                    dataControler.budgetData.setBudget(newBudget);
                                    budget_edt.setText(String.valueOf(dataControler.budgetData.getBudget()));
                                    progressBar.setProgress(scale.intValue());
                                    new AlertDialog.Builder(BudgetActivity.this).setTitle("設定新預算").setMessage("設定成功!!").setNegativeButton("OK",null).show();
                                }
                            }
                        }
                        String budget = setbudget_edt.getText().toString();
                        String scale = df.format((dataControler.budgetData.getTotal_expense() / Double.parseDouble(budget))*100);
                        String sql = "UPDATE `icoco`.`budget` SET `budget`='"+budget+"',`scale`='"+scale+"' WHERE `uid`='"+dataControler.userData.getId()+"';";
                        new updateBudget().execute(sql);
                    }
                }).create();
    }

    private void setOnClickListener()
    {
        budget_edt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setBudgetDialog.show();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                selectType = chooseType_en[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        expenseAdd_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!expenseAmt_edt.getText().toString().isEmpty())
                {

                    Double newTotal_expense = dataControler.budgetData.getTotal_expense()+Double.parseDouble(expenseAmt_edt.getText().toString());
                    Double scale = Double.parseDouble(df.format((newTotal_expense / dataControler.budgetData.getBudget())*100 ));

                    String sql = "INSERT INTO `icoco`.`expense_log` (`uid`, `expense`, `type`) VALUES ('"+dataControler.userData.getId()+"', '"+expenseAmt_edt.getText().toString()+"', '"+selectType+"');";
                    new MySQL_Execute().execute(sql);
                    sql = "UPDATE `icoco`.`budget` SET `total_expense`='"+newTotal_expense+"', `scale`='"+String.valueOf(scale)+"' WHERE `uid`='"+dataControler.userData.getId()+"';";
                    new MySQL_Execute().execute(sql);

                    dataControler.budgetData.loadExpense_log();
                    dataControler.budgetData.setTotal_expense(newTotal_expense);
                    total_expense_edt.setText(String.valueOf(dataControler.budgetData.getTotal_expense()));
                    progressBar.setProgress(dataControler.budgetData.getScale().intValue());
                }
            }
        });


//        getCoupon_btn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                new AutoDetectionCoupon_Budget().startDetection();
//            }
//        });
    }

    private void findView()
    {
        budget_edt = (EditText) findViewById(R.id.budget_edt);
        LayoutInflater inflater = LayoutInflater.from(BudgetActivity.this);
        set_budget_view = inflater.inflate(R.layout.set_budget, null);
        total_expense_edt = (EditText) findViewById(R.id.total_expense_edt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        spinner = (Spinner) findViewById(R.id.spinner);
        expenseAmt_edt = (EditText) findViewById(R.id.expenseAmt_edt);
        expenseAdd_btn = (Button) findViewById(R.id.expenseAdd_btn);
        percent_txv = (TextView) findViewById(R.id.percent_txv);
//        getCoupon_btn = (Button) findViewById(R.id.getCoupon_btn);
    }
}
