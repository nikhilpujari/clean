package com.example.aniksarder.keepclean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.aniksarder.keepclean.UserActivity.LoginActivity;
import com.example.aniksarder.keepclean.UserActivity.RegistrationActivity;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewRegistration;
    private TextView mTextViewSignIn;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        mTextViewRegistration = findViewById(R.id.TextViewRegistration);
        mTextViewSignIn = findViewById(R.id.TextViewSignIn);

        mTextViewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(regIntent);
            }
        });
        mTextViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(signIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("EXIT !").setMessage("Are you sure to eixts?").setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        System.exit(0);
                    }

                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
