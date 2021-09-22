package com.example.aniksarder.keepclean;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aniksarder.keepclean.UserActivity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private EditText mPasswordEmail;
    private Button mResetPassBtn;
    private Toolbar mToolBar;

    private FirebaseAuth firebaseAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mPasswordEmail = findViewById(R.id.EditTextForgetPassword);
        mResetPassBtn = findViewById(R.id.bottonResetpass);
        mToolBar = findViewById(R.id.PasswordToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" Reset Password ");
        firebaseAuth = FirebaseAuth.getInstance();

        mResetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = mPasswordEmail.getText().toString().trim();
                if (userEmail.equals("")){
                    Toast.makeText(PasswordActivity.this,"Please enter your registered email ID ",Toast.LENGTH_LONG).show();
                }else {
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(PasswordActivity.this," Password reset email send ! ",Toast.LENGTH_LONG).show();
                                finish();
                                //startActivity(new Intent(mContext,LoginActivity.class));
                                Intent homeIntent = new Intent(PasswordActivity.this, HomeActivity.class);
                                startActivity(homeIntent);

                            }else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(PasswordActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                                //Toast.makeText(PasswordActivity.this,"Error in sendind password reset email ",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }



}
