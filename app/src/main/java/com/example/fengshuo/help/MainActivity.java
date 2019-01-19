package com.example.fengshuo.help;

import java.util.Date;
import java.util.List;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;

import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;




public class MainActivity extends AppCompatActivity {

    private Button msetInfo;
    private Button msetMethod;
    private ImageButton mone;
    private LocationManager locationManager;
    private String locationProvider;
    private TextView postionView;

    private SensorManager sensorManager;
    private MySensorEventListener sensorEventListener;
    private static OnepxReceiver mOnepxReceiver;

    private int count=0;

    public static int sign=0;

    private String time1;
    private String time2;
    private String time3;

    private Long t1;
    private Long t2;
    private Long t3;

    private Long now1= Long.parseLong("0");
    private Long now2= Long.parseLong("0");
    private Long now3= Long.parseLong("0");

    private Long n1;
    private Long n2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("onCreate");
        sensorEventListener=new MySensorEventListener();
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        msetInfo=(Button)findViewById(R.id.setInfo);
        msetMethod=(Button)findViewById(R.id.setMethod);
        mone=(ImageButton)findViewById(R.id.one);
        postionView=(TextView)findViewById(R.id.positionView);
        msetInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FirstActivity.class);
                startActivity(intent);
            }
        });
        msetMethod.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sign=1;
                Intent intent=new Intent(MainActivity.this,SetActivity.class);
                startActivity(intent);
            }
        });
        mOnepxReceiver=new OnepxReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(mOnepxReceiver,intentFilter);
        mone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        final IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver,filter);
        final IntentFilter filter2=new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBatInfoReceiver,filter2);
        
    }

    //监听获取电源键
    private final BroadcastReceiver mBatInfoReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            FileInputStream inStream;
            try {
                inStream = openFileInput("time.text");
                byte[] buffer = new byte[1024];
                int hasRead = 0;
                StringBuilder data = new StringBuilder();
                while ((hasRead = inStream.read(buffer)) != -1) {
                    data.append(new String(buffer, 0, hasRead));
                }
                inStream.close();
                time1 = data.substring(0, data.indexOf("*"));
                time1 = time1.substring(1);
                time2 = data.substring(data.indexOf("*"), data.indexOf("&"));
                time2 = time2.substring(1);
                t1=Long.parseLong(time1);
                t2=Long.parseLong(time2);

                final String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_SCREEN_ON.equals(action)) {
                    if (sign == 0) {
                        Date dt =new Date();
                        if(now1!=0&&now2!=0&&now2-now1>5000)//超时处理
                        {
                            count=0;
                        }
                        if(now2!=0&&now3!=0&&now3-now2>5000)
                        {
                            count=0;
                        }
                        if(now1!=0&&now3!=0&&now1>now3)
                        {
                            count=0;
                        }
                        count++;

                        switch (count){
                            case 1:
                                now1=dt.getTime();
                              //  System.out.println("now1"+now1);
                                break;
                            case 2:
                                now2=dt.getTime();
                              //  System.out.println("now2"+now2);
                                break;
                            case 3:
                                now3=dt.getTime();
                              //  System.out.println("now3"+now3);
                                break;
                        }
                        if(count>3)
                        {
                            count=0;
                        }
                        if(now1>0&&now2>0&&now3>0)
                        {
                            n1=now2-now1;
                            n2=now3-now2;
                            if(n1>=t1-2000&&n1<=t1+2000&&n2>=t2-2000&&n2<=t2+2000)
                            {
                                SendMessage();
                                System.out.println("间隔时间："+n1+"#"+n2);
                                System.out.println("发送成功");
                                now1= Long.parseLong("0");
                                now2=Long.parseLong("0");
                                now3=Long.parseLong("0");
                            }
                            else {
                                System.out.println("发送失败");
                                now1= Long.parseLong("0");
                                now2=Long.parseLong("0");
                                now3=Long.parseLong("0");
                            }
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void onResume()
    {
        Sensor accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    public final class MySensorEventListener implements SensorEventListener
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER)
            {
                double x=event.values[SensorManager.DATA_X];
                double y=event.values[SensorManager.DATA_Y];
                double z=event.values[SensorManager.DATA_Z];
                double a=Math.sqrt(x*x+y*y+z*z);
                if(a>30)//500g死亡加速度
                {
                    System.out.println(a);
                    SendMessage();
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor,int accuracy)
        {

        }
    }

    public void SendMessage()
    {
        FileInputStream inStream;
        String phone;
        String message;
        try {
            /*************获取联系人手机号与求救信息**************/
            inStream=openFileInput("new4file.text");
            byte[] buffer=new byte[1024];
            int hasRead=0;
            StringBuilder data=new StringBuilder();
            while((hasRead=inStream.read(buffer))!=-1){
                data.append(new String(buffer,0,hasRead));
            }
            inStream.close();
            phone=data.substring(data.indexOf("*"),data.indexOf("&"));
            phone=phone.substring(1);
            message=data.substring(data.indexOf("&"),data.indexOf("#"));
            message=message.substring(1);
            System.out.println(message);
            /***************获取位置信息****************/
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1);
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                //ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

                String serviceString=Context.LOCATION_SERVICE;
                LocationManager locationManager=(LocationManager)getSystemService(serviceString);
                String provider;
                List<String> providerList = locationManager.getProviders(true);
                if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                } else {
                    Toast.makeText(this, "No Location provider to use", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location=locationManager.getLastKnownLocation(provider);
                double lat;
                double lng;
                System.out.println("获取位置信息2");
                System.out.println(location.getLatitude());
                Log.d("location","NO");
                Log.d("location",Double.toString(location.getLatitude()));
               // lat = location.getLatitude();//纬度
               // lng = location.getLongitude();//经度

                lat=37;
                lng=48;
                String addressStr = "no address \n";
                Geocoder geocoder = new Geocoder(this);
                    List<Address> addresses = geocoder.getFromLocation(lat,
                            lng, 1);
                    StringBuilder sb = new StringBuilder();
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(" ");
                        }
                sb.append(address.getCountryName());
                        message="我在"+address.getCountryName()+" "+address.getAddressLine(0)+" "+
                                address.getAddressLine(1)+"。经纬度是"+lat+","+lng+"。"+message;
                Log.i("location", "address.getCountryName()==" + address.getCountryName());//国家名
                        sb.append(address.getAddressLine(0));
                        Log.i("location", "address.getAddressLine(0)=3=" + address.getAddressLine(0));
                        sb.append(address.getAddressLine(1));
                        Log.i("location", "address.getAddressLine(1)=4=" + address.getAddressLine(1));
                        addressStr = sb.toString();
                    }


               // SmsManager smsManager=SmsManager.getDefault();
               // smsManager.sendTextMessage(phone,null,message,null,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

