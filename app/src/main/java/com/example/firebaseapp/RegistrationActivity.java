package com.example.firebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    EditText etName,etEmail,etPassword;
    Button btnRegister;
    TextView tvSignIn;

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        UISetupView();
        mAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    progressDialog.setMessage("Registering You...");
                    progressDialog.show();

                    String user_email=etEmail.getText().toString().trim();
                    String user_password=etPassword.getText().toString().trim();

                    mAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                sendUserData();
                                Toast.makeText(RegistrationActivity.this,"Registration successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this,"Registration failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(RegistrationActivity.this,"Please fill in all the fields and password should be 6 characters long.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });


    }

    private void UISetupView(){
        etName=(EditText) findViewById(R.id.etName);
        etPassword=(EditText) findViewById(R.id.etPassword);
        etEmail=(EditText) findViewById(R.id.etEmail);
        btnRegister=(Button) findViewById(R.id.btnRegister);
        tvSignIn=(TextView) findViewById(R.id.tvSignIn);
    }

    private boolean validate(){
        boolean result;

        String name=etName.getText().toString().trim();
        String email=etEmail.getText().toString().trim();
        String password=etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty())
           return false;
        else {
            if (password.length() >= 6)
                return true;
            else
                return false;
        }
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users/"+mAuth.getUid());
        myRef.child("name").setValue(etName.getText().toString());
        myRef.child("email").setValue(etEmail.getText().toString());
    }

    @Override
    public void onBackPressed(){
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }
}
