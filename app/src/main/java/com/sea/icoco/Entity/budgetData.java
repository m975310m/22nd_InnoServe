package com.sea.icoco.Entity;

import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/10/1.
 */


public class budgetData
{
    JSONObject budgetData;
    JSONArray expense_log;
    DataControler dataControler = MainActivity.dataControler;
    Double budget,total_expense,scale;
    boolean loadBudgetDataSuccess = false;

    public void loadBudget()
    {
        loadBudgetData();
        loadExpense_log();
    }

    public void loadBudgetData()
    {
        class SelectBudgetData extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    if(!result.equals("[]"))
                    {
                        budgetData = new JSONArray(result).getJSONObject(0);
                    }
                    else
                    {
                        String sql = "INSERT INTO `icoco`.`budget` (`uid`, `budget`, `total_expense`, `scale`) VALUES ('"+dataControler.userData.getId()+"', '0', '0', '0');";
                        new MySQL_Execute().execute(sql);
                        budgetData = new JSONObject();
                        budgetData.put("budget","0");
                        budgetData.put("total_expense","0");
                        budgetData.put("scale","0");
                    }

                    budget = Double.parseDouble(budgetData.getString("budget"));
                    total_expense = Double.parseDouble(budgetData.getString("total_expense"));
                    scale = Double.parseDouble(budgetData.getString("scale"));
                    Log.d("Debug BudgetData","BudgetData Load Success !!");
                    loadBudgetDataSuccess = true;
                } catch (JSONException e) { Log.e("Debug SelectBudgetData",e.toString()); }
            }
        }

        String sql = "SELECT * FROM icoco.budget where uid = "+dataControler.userData.getId()+";";
        new SelectBudgetData().execute(sql);
    }

    public void loadExpense_log()
    {
        class SelectExpenseLog extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    expense_log = new JSONArray(result);
                } catch (JSONException e) { Log.e("Debug SelectExpenseLog",e.toString()); }
            }
        }

        String sql = "SELECT * FROM icoco.expense_log where uid = "+dataControler.userData.getId()+";";
        new SelectExpenseLog().execute(sql);
    }


    public Double getBudget() { return budget; }
    public Double getTotal_expense() { return total_expense; }
    public Double getScale()
    {
        Double scale = 100.00;
        try
        {
            DecimalFormat df = new DecimalFormat("#.##");
            scale = Double.parseDouble(df.format((total_expense / budget) * 100));
        }catch (Exception e){}
        return scale;
    }
    public JSONArray getExpense_log() { return expense_log; }
    public void setBudget(Double budget) { this.budget = budget; }
    public void setTotal_expense(Double total_expense) { this.total_expense = total_expense; }
//    public void setScale(Double scale) { this.scale = scale; }
}
