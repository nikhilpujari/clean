package com.example.aniksarder.keepclean.UserActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aniksarder.keepclean.HomeActivity;
import com.example.aniksarder.keepclean.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mRegEditTextEmail;
    private EditText mRegEditTextPass;
    private EditText mRegEditTextConfirmPass;
    private Button mRegisterBtn;
    private Button mBtnLoginLink;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);
        initViews();

        mAuth = FirebaseAuth.getInstance();

        mBtnLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendTologin();
            }
        });


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mRegEditTextEmail.getText().toString().trim();
                String password = mRegEditTextPass.getText().toString().trim();
                String con_password = mRegEditTextConfirmPass.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(con_password)){
                    if(!isConnected(mContext)) buildDialog(mContext).setCancelable(false).show();
                    else {

                    }
                    if (password.equals(con_password)){
                        mProgressDialog.setMessage("Sign Up ....");
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();

                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    Intent setupIntent = new Intent(RegistrationActivity.this, ProfileSetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();


                                }else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegistrationActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                                }
                                mProgressDialog.dismiss();
                            }
                        });

                    }else
                        {
                        Toast.makeText(mContext,"Password doesn't match",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    private void initViews(){
        mRegEditTextEmail = findViewById(R.id.EditTextEmail);
        mRegEditTextPass = findViewById(R.id.EditTextPassword);
        mRegEditTextConfirmPass = findViewById(R.id.EditTextConfirmPass);
        mRegisterBtn = findViewById(R.id.ButtonRegister);
        mBtnLoginLink = findViewById(R.id.btnViewLoginLink);

    }


    private void SendToHome() {
        Intent homeIntent = new Intent(RegistrationActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();

    }

    private void SendTologin() {
        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection !");
        builder.setMessage("You have no internet connection");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        return builder;
    }


}
