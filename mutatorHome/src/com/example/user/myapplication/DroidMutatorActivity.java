package com.example.user.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class DroidMutatorActivity extends AppCompatActivity {
    Button btn;
    TextView textView;
    Handler handler = new Handler();
    private int COMPUTE_FINISHED = 1;
    long[] datas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_droid_mutator);
        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);
        btn.setOnClickListener(new Listener());
    }
    class Listener implements View.OnClickListener{
        public void onClick(View view){
            if(needComputeData()){
                setTextView("computing...");
                new DroidMutatorThread().start();
            }
        }
    }
    class DroidMutatorThread extends Thread{
        public void run(){
            datas = getDatas();
            // datas[0] = 0;
            computeData(datas);
            Message msg = Message.obtain();
            msg.what = COMPUTE_FINISHED;
            handler.sendMessage(msg);
        }
    }
    private void setTextView(String str){
        textView.setText(str);
    }

    //==============================================================
    public long[] getDatas(){
        return new long[10];
    }

    private boolean needComputeData(){
        return true;
    }
    private void computeData(long[] datas){
        System.out.println(datas.toString());
    }
}
