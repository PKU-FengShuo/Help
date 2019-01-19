package com.example.fengshuo.help;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;

import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by fengshuo on 2018/5/2.
 */
public class SetActivity extends AppCompatActivity {
    private Button ensure;
    private int count=0;
    private Long time1= Long.parseLong("0");
    private Long time2= Long.parseLong("0");
    private Long time3= Long.parseLong("0");

    private Long t1= Long.parseLong("0");
    private Long t2= Long.parseLong("0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        ensure=(Button)findViewById(R.id.ensure);
        ensure.setOnClickListener(mListener);

        final IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver,filter);
        final IntentFilter filter2=new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBatInfoReceiver,filter2);
    }

    private final BroadcastReceiver mBatInfoReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action=intent.getAction();
            if(Intent.ACTION_SCREEN_OFF.equals(action)||
                    Intent.ACTION_SCREEN_ON.equals(action)){
                if(MainActivity.sign==1&&(time1==0||time2==0||time3==0))
                {
                    Date dt =new Date();
                    count++;
                    switch (count){
                        case 1:
                            time1=dt.getTime();
                            break;
                        case 2:
                            time2=dt.getTime();
                            break;
                        case 3:
                            time3=dt.getTime();
                    }
                    if(count>3)
                    {
                        count=0;
                    }
                }

            }
        }
    };

    View.OnClickListener mListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println(time1);
            System.out.println(time2);
            System.out.println(time3);

            t1=time2-time1;
            t2=time3-time2;
            String time=t1.toString()+"*"+t2.toString()+"&";
            FileOutputStream outputStream;
            try{
                outputStream=openFileOutput("time.text",Context.MODE_PRIVATE);
                outputStream.write(time.getBytes());
                outputStream.flush();
                outputStream.close();
                MainActivity.sign=0;
                Intent intent=new Intent(SetActivity.this,MainActivity.class);
                startActivity(intent);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    };


}
