package com.example.a502.smarthome;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        setting();
    }

    public void setting(){
        editText = findViewById(R.id.editText);
    }
    public void onClickClear(View v){
        editText.setText("");
    }
}
