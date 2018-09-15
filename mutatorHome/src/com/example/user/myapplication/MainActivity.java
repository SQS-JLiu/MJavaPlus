package com.example.user.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Button buttonLogin,buttonReset;
    private EditText editTextId,editTextPassword;
    private int data = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin =(Button) findViewById(R.id.btn_login);
        buttonReset = (Button)findViewById(R.id.btn_reset);
        editTextId = (EditText)findViewById(R.id.et_id);
        editTextPassword = (EditText)findViewById(R.id.et_passwd);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = editTextId.getText().toString()+":"+editTextPassword.getText().toString();
                Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("msg","I'm in second activity.");
                Intent intent = new Intent();
                intent.setAction("android.intent.activity_second.action");
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                editTextId.setText("a");
                editTextPassword.setText("");
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("progress");
                dialog.show();
                test();
                new AsyncTask<Long, Void, Void>() {

                    protected void onPreExecute() {
                        //dialog.setMax(10);
                        Toast.makeText(MainActivity.this,"Starting progress...",Toast.LENGTH_SHORT).show();
                        super.onPreExecute();
                    }
                    protected Void doInBackground(Long... args) {
                        for(int i=0;i<1;i++){
                            try{
                                Thread.sleep(500);
                            }catch (Exception e){

                            }

                        }
                        //dialog.incrementProgressBy(1);
                        return null;
                    }
                    protected void onPostExecute(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,"After progress...",Toast.LENGTH_SHORT).show();
                    }
                }.execute(2L);
            }
        });
    }

    public void test(){
        new Thread(){
            public void run(){
                TestDemo testDemo = new TestDemo();
                int a=1,b=2,c=3,d=0;
                int[] array = new int[10];
                for (int i = 0; i < 10; i++) {
                    array[i] = 0;
                }
                arrayFunc(array,testDemo);
                if (a > b) {
                    if (a > c) {
                        a++;
                    }
                    else {
                        c++;
                        d = (c > 0 ? a : b);
                    }
                }
                else {
                    if (b > 0 || c > 0) {
                        b++;
                    }
                }
                System.out.println("test...");
            }
        }.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String demoStr = "demo";
                printString(demoStr);
                System.out.println("test2...");
            }
        });
        System.out.println("I'm Test:"+data);
    }

    class TestAsynctask extends  AsyncTask<Long, Void, Void>{

        private int a = 1;
        private int b = 2;

        protected Void doInBackground(Long... args) {
            final int maxNum = max(a,b);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("test3..."+max(a,10));
                }
            });
            return null;
        }
    }

    public static void arrayFunc(int arr[],TestDemo testDemo) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[1]);
        }
    }

    public void printString(String str){
        System.out.println("test2..."+str);
    }

    public int max(int a,int b){
        return   a > b ? a : b;
    }
}
