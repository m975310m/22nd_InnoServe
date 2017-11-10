package com.sea.icoco;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.step.UpdateUiCallBack;
import com.sea.icoco.step.service.StepService;
import com.sea.icoco.step.utils.SharedPreferencesUtils;
import com.sea.icoco.view.StepArcView;


public class RunpointActivity extends AppCompatActivity {
    private boolean isBind = false;
    private StepArcView cc;
    private SharedPreferencesUtils sp;
    private TextView point;
    DataControler dataControler = MainActivity.dataControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runpoint);
        findView();
        initData();
        setupService();
        point.setText(String.valueOf(dataControler.getPoint()));
    }


    private void findView()
    {
        cc = (StepArcView) findViewById(R.id.cc);
        point = (TextView) findViewById(R.id.PointText);
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "20");
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
//        tv_isSupport.setText("计步中...");
    }
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
    //    * 用于查询应用服务（application Service）的状态的一种interface，
//            * 更详细的信息可以参考Service 和 context.bindService()中的描述，
//            * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
//            */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            final StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "20");
            cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepService.getStepCount());

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "20");
                    if (stepCount > Integer.parseInt(planWalk_QTY)){
                        dataControler.setPoint(dataControler.getPoint()+1);
                        Log.d("點數", String.valueOf(dataControler.getPoint()));
                        stepService.setStepCount(1);
                        stepCount = 1;
                        point.setText(String.valueOf(dataControler.getPoint()));
                    }
                    cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
//                    Log.d("點數","最大步:"+planWalk_QTY);
                    Log.d("stepCount:", String.valueOf(stepCount));
                }

            });

        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
