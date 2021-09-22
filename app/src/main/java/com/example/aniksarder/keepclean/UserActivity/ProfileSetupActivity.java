package com.example.aniksarder.keepclean.UserActivity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aniksarder.keepclean.BottomNavigationViewHelper;
import com.example.aniksarder.keepclean.HomeActivity;
import com.example.aniksarder.keepclean.MainActivity;
import com.example.aniksarder.keepclean.PostActivity;
import com.example.aniksarder.keepclean.R;
import com.example.aniksarder.keepclean.UserList.UserListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText mUserName;
    private EditText mUserNumber;
    private Button mBtnSave;
    private Context mContext;
    private Toolbar setupToolbar;

    private BottomNavigationView mainbottomNav;

    private ProgressDialog mProgressDialog;
    private CircleImageView mProfileImage;

    private Uri mainImageURI = null;
    private String user_id;
    private boolean isChanged = false;


    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);
        setupToolbar = findViewById(R.id.setuptoolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setting");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mUserName = findViewById(R.id.userName);
        mUserNumber = findViewById(R.id.userPhNumber);
        mBtnSave = findViewById(R.id.buttonUserInfo);
        mProfileImage = findViewById(R.id.profileImage);
        mainbottomNav = findViewById(R.id.bottomNavigation);

        BottomNavigationViewHelper.disableShiftMode(mainbottomNav);
        Menu menu = mainbottomNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        //menuItem.setVisible(false);



        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_action_home:
                        Intent homeIntent = new Intent(ProfileSetupActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        return true;

                    case R.id.bottom_action_add:
                        Intent postIntent = new Intent(ProfileSetupActivity.this, PostActivity.class);
                        startActivity(postIntent);
                        return true;

                    case R.id.bottom_action_account:

                        return true;

                    default:
                        return false;


                }
            }
        });

        mBtnSave.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String number = task.getResult().getString("number");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        mUserName.setText(name);
                        mUserNumber.setText(number);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.profile_image);

                        Glide.with(ProfileSetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(mProfileImage);


                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileSetupActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                mBtnSave.setEnabled(true);

            }
        });


        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = mUserName.getText().toString();
                final String user_number = mUserNumber.getText().toString();

                if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(user_number) && mainImageURI != null) {

                    mProgressDialog.setMessage("Save Data ....");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name , user_number);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(ProfileSetupActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    mProgressDialog.dismiss();

                                }
                            }
                        });

                    } else {

                        storeFirestore(null, user_name , user_number );

                    }

                }

            }

        });


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(ProfileSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(ProfileSetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(ProfileSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });


    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name, String user_number) {

        Uri download_uri;

        if(task != null) {

            download_uri = task.getResult().getDownloadUrl();

        } else {

            download_uri = mainImageURI;

        }

        Map<String, String> userMap = new HashMap<>();

        userMap.put("name", user_name);
        userMap.put("number", user_number);
        userMap.put("image", download_uri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(ProfileSetupActivity.this, "User data updated.", Toast.LENGTH_LONG).show();
                    Intent homeIntent = new Intent(ProfileSetupActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileSetupActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

                mProgressDialog.dismiss();

            }
        });


    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ProfileSetupActivity.this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                mProfileImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
