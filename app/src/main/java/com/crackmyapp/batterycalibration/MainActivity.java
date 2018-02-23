package com.crackmyapp.batterycalibration;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    Context Context;
    TextView textView1, textView2, battery_percentage;
    ImageView image;
    Animation animationBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkRoot();
        checkFirstRun();
        animationBlink = AnimationUtils.loadAnimation(this, R.anim.blink);

        image = findViewById(R.id.battery);

        // Get the application context
        Context = getApplicationContext();

        // Initialize a new IntentFilter instance
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        // Register the broadcast receiver
        Context.registerReceiver(mBroadcastReceiver, iFilter);

        // Get the widgets reference from XML main
        battery_percentage = findViewById(R.id.battey_percentage);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);



}

    public void checkRoot() {
        Process executor = null;
        try {
            executor = Runtime.getRuntime().exec("su -c ls /data/data");
            executor.waitFor();
            int iabd = executor.exitValue();
            if (iabd != 0) {
                /*process exit value is not 0, so user is not root*/
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Battery Calibration Guide");
                alertDialog.setMessage("***ROOT REQUIRED***");
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String charging_status="",battery_condition="",power_source="Unplugged";

            // Get the battery percentage
            int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);

            // Get the battery condition
            int  health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);

            if(health == BatteryManager.BATTERY_HEALTH_COLD)
            {
                battery_condition = "Cold";
            }
            if (health == BatteryManager.BATTERY_HEALTH_DEAD)
            {
                battery_condition = "Dead";
            }
            if (health == BatteryManager.BATTERY_HEALTH_GOOD)
            {
                battery_condition = "Good";
            }
            if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT)
            {
                battery_condition = "Over Heat";
            }
            if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE)
            {
                battery_condition = "Over Voltage";
            }
            if(health == BatteryManager.BATTERY_HEALTH_UNKNOWN)
            {
                battery_condition = "Unknown";
            }
            if(health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE)
            {
                battery_condition = "Unspecified failure";
            }

            // Get the battery temperature in celcius
            int  temperature_c= (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;

            //  Celsius to Fahrenheit battery temperature conversion
            int temperature_f = (int)(temperature_c*1.8+32);

            // Get the battery power source
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            if(chargePlug == BatteryManager.BATTERY_PLUGGED_USB)
            {
                power_source = "USB";
            }
            if(chargePlug == BatteryManager.BATTERY_PLUGGED_AC)
            {
                power_source = "AC Adapter";
            }
            if(chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            {
                power_source = "Wireless";
            }

            // Get the status of battery Eg. Charging/Discharging
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            if(status == BatteryManager.BATTERY_STATUS_CHARGING)
            {
                charging_status = "Charging";
                image.setImageResource(R.drawable.battert_icon);
                image.startAnimation(animationBlink);
            }

            if(status == BatteryManager.BATTERY_STATUS_DISCHARGING)
            {
                charging_status = "Not Charging";
                image.clearAnimation();
                if (level>=60 && level<=70){
                    image.setImageResource(R.drawable.ic_battery_60);
                    image.clearAnimation();
                }else if (level>=40 &&level<=60){
                    image.setImageResource(R.drawable.ic_battery_50);
                    image.clearAnimation();
                }else if (level>=20 && level<=40){
                    image.setImageResource(R.drawable.ic_battery_30);
                    image.clearAnimation();
                }else if (level>=15 && level<=20){
                    image.setImageResource(R.drawable.ic_battery_20);
                    image.clearAnimation();
                }else if (level>=0 && level<=15){
                    image.setImageResource(R.drawable.ic_battery_15);
                    image.clearAnimation();
                }else if (level>=70 && level<=80){
                    image.setImageResource(R.drawable.ic_battery_80);
                    image.clearAnimation();
                }else if (level>=80 && level<=90){
                    image.setImageResource(R.drawable.ic_battery_80);
                    image.clearAnimation();
                }else if (level<=90 && level<=100){
                    image.setImageResource(R.drawable.ic_battery_full);
                    image.clearAnimation();
                }else if (status== BatteryManager.BATTERY_STATUS_CHARGING){
                    image.setImageResource(R.drawable.battert_icon );
                    image.clearAnimation();
                }else image.clearAnimation();
            }

            if (status == BatteryManager.BATTERY_STATUS_FULL)
            {
                charging_status = "Battery Full";
               notifyme();
            }

            if(status == BatteryManager.BATTERY_STATUS_UNKNOWN)
            {
                charging_status = "Unknown";
            }

            if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
            {
                charging_status = "Not Charging";
            }

            // Get the battery technology
            String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

            // Get the battery voltage
            int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);

            //Display the output of battery Status
            battery_percentage.setText("Battery Percentage: "+level+"%");

            textView1.setText("Battery Condition:\n\n"+
                    "Battery Temperature:\n\n"+
                    "Power Source:\n\n"+
                    "Charging Status:\n\n"+
                    "Battery Type:\n\n"+
                    "Voltage:");

            textView2.setText(battery_condition+"\n\n"+
                    temperature_c+" "+ (char) 0x00B0 +"C / "+ temperature_f +" "+ (char) 0x00B0 +"F\n\n"+
                    power_source+"\n\n"+
                    charging_status+"\n\n"+
                    technology+"\n\n"+
                    voltage+"mV"



            );

        }


    };

    public void notifyme() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_battery_full)
                        .setContentTitle("Battery full !")
                        .setContentText("Disconnect charger and calibrate battery.");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public boolean superuser(String command) {
        try {
            return Runtime.getRuntime().exec(new String[]{"su", "-c", command}).waitFor() == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e2) {
            return false;
        }
    }
    public void deleteFile(View view) {
        checkRoot();
        String path = "/data/system/" + "batterystats.bin";
        File f = new File(path);
        if (f.exists()) {
            superuser("rm " + ("/data/system/" + "batterystats.bin"));
            Toast.makeText(getApplicationContext(), "Battery Calibration Succesfully", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(), "Battery Calibration Failed\nbatterystats.bin not found !", Toast.LENGTH_LONG).show();
        }
    }
    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Battery Calibration Guide");
            alertDialog.setMessage("***ROOT REQUIRED***\n\nPlease charge your phone to 100% and then click on calibrate.");
            alertDialog.setIcon(R.drawable.ic_launcher);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setButton("Got it.", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
                    });
            alertDialog.show();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }
}
