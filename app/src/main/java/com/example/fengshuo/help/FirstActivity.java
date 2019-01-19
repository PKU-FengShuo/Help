package com.example.fengshuo.help;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class FirstActivity extends AppCompatActivity {

    private EditText name;
    private EditText phone;
    private EditText message;
    private Button save;
    private Button look;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        name=(EditText)findViewById(R.id.name);
        phone=(EditText)findViewById(R.id.phone);
        message=(EditText)findViewById(R.id.message);
        save=(Button)findViewById(R.id.save);
        save.setOnClickListener(mListener);
    }
    OnClickListener mListener=new OnClickListener() {
        @Override
        public void onClick(View v) {

            String Mname=name.getText().toString().trim()+'*';
            String Mphone=phone.getText().toString().trim()+'&';
            String Mmessage=message.getText().toString().trim()+'#';
            FileOutputStream outputStream;
            try{
                outputStream=openFileOutput("new4file.text",Context.MODE_PRIVATE);
                outputStream.write(Mname.getBytes());
                outputStream.write(Mphone.getBytes());
                outputStream.write(Mmessage.getBytes());
                outputStream.flush();
                outputStream.close();
                Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                startActivity(intent);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
}
