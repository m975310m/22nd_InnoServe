package com.sea.icoco;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;


import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.sea.icoco.R;

import java.util.Timer;
import java.util.TimerTask;

public class GoPointActivity extends AppCompatActivity {
    private Timer timer;
    CircularProgressBar circularProgressBar;
    int step = 4;
    int point = 37;
    int end_step = 10;
    int getPoint = 1;
    float progress = ((float)step/(float)end_step)*100;
    TextView point_text,setp_txv,bonus_txv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_point);
        findView();
        // https://github.com/lopspower/CircularProgressBar


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progress >= 100){
                            step = 0;
                            point+=1;
                            point_text.setText(String.valueOf(point) + " 點");
                            bonus_txv.setTextColor(android.graphics.Color.RED);
                            bonus_txv.setText("獲得點數 "+String.valueOf(getPoint)+" 點");
                            bonus_txv.setTextSize(40);

                            timer.cancel();
                        }
                        step++;
                        progress = ((float)step/(float)end_step)*100;
                        setp_txv.setText("累積步數 "+String.valueOf(step)+" / "+String.valueOf(end_step)+" 步");
                        circularProgressBar.setProgress(progress);

                    }
                });
            }
        }, 0, 1200);
    }

    private void findView() {
        circularProgressBar = (CircularProgressBar)findViewById(R.id.progress);
        point_text = (TextView) findViewById(R.id.point_text);
        setp_txv = (TextView) findViewById(R.id.setp_txv);
        bonus_txv = (TextView) findViewById(R.id.bonus_txv);
    }
}
