package com.example.user.myapplication;

import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends MainActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = (TextView)findViewById(R.id.tv_show);
        Bundle bundle = this.getIntent().getExtras();
        String msg = bundle.getString("msg");
        textView.setText(msg);
    }
}
