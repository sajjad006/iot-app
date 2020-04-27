package com.example.firebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    TextView lightstatus,fanstatus,doorstatus, acstatus, tvName, tvTemp, tvHumid;
    Switch homeLeave;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            final String userid= firebaseAuth.getUid();

            lightstatus = (TextView) findViewById(R.id.lightstatus);
            fanstatus   = (TextView) findViewById(R.id.fanstatus);
            doorstatus  = (TextView) findViewById(R.id.doorstatus);
            acstatus    = (TextView) findViewById(R.id.acstatus);
            tvName      = (TextView) findViewById(R.id.tvName);
            tvHumid     = (TextView) findViewById(R.id.tvHumidity);
            tvTemp      = (TextView) findViewById(R.id.tvTemp);
            homeLeave   = (Switch) findViewById(R.id.switchLeave);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String light = dataSnapshot.child("light").getValue(String.class);
                    String fan   = dataSnapshot.child("fan").getValue(String.class);
                    String door  = dataSnapshot.child("door").getValue(String.class);
                    String ac    = dataSnapshot.child("ac").getValue(String.class);

                    String temp = dataSnapshot.child("temperature").getValue(String.class);
                    String humidity = dataSnapshot.child("humidity").getValue(String.class);

                    String name = dataSnapshot.child("Users").child(userid).child("name").getValue(String.class);

                    Log.v("Firebase Data", "Light status:" + light);
                    Log.v("Firebase Data", "Fan status:" + fan);
                    Log.v("Firebase Data", "Door status:" + door);

                    lightstatus.setText(convert(light));
                    fanstatus.setText(convert(fan));
                    doorstatus.setText(convert(door));
                    acstatus.setText(convert(ac));
                    tvName.setText("WELCOME "+name.toUpperCase());
                    tvTemp.setText("Temperature : " + temp + (char) 0x00B0 + "C");
                    tvHumid.setText("Humidity : " + humidity + "%");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.v("Sajjad", "Failed to read data.");
                }
            });

            homeLeave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        myRef.child("homeLeave").setValue("1");
                    }
                    else{
                        myRef.child("homeLeave").setValue("0");
                    }
                }
            });
        }
        else{
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutMenu:
                Logout();
                break;
            case R.id.subscribeMenu:
                //fcm subscribe to topic
                FirebaseMessaging.getInstance().subscribeToTopic("weather")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Successfully subscribed for notifications";
                                if (!task.isSuccessful()) {
                                    msg = "Sorry! Could not subscribe to notifications";
                                }
                                Log.d("msg_status", msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String convert(String data){
        String result="";
        if(data.equals("1"))
            result="ON";
        else if(data.equals("0"))
            result="OFF";
        return result;
    }

    public void lighton (View view){
        myRef.child("light").setValue("1");
    }
    public void lightoff (View view){
        myRef.child("light").setValue("0");
    }

    public void fanon (View view){
        myRef.child("fan").setValue("1");
    }
    public void fanoff (View view){
        myRef.child("fan").setValue("0");
    }

    public void acon (View view){
        myRef.child("ac").setValue("1");
    }
    public void acoff (View view){
        myRef.child("ac").setValue("0");
    }

    public void dooron (View view){
        myRef.child("door").setValue("1");
    }
    public void dooroff (View view){
        myRef.child("door").setValue("0");
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No",null)
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }
}