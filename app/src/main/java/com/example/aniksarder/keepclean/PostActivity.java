package com.example.aniksarder.keepclean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aniksarder.keepclean.UserActivity.ProfileSetupActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class PostActivity extends AppCompatActivity {

    private ImageView mPlaceIcon;
    private ImageView mSelectImage;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private Toolbar mToolBar;
    private TextView mPlaceName;
    private TextView mAddress;
    private Context mContext;
    private ProgressDialog mProgress;

    private Uri postImageUri = null;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    //private BottomNavigationView mainbottomNav;

    private String current_user_id;
    private Bitmap compressedImageFile;

    private static final int MY_PERMISSION_FINE_LOCATION = 101;
    private static final int PLACE_PICKER_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mContext = this;
        initViews();
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" New Post ");
        mProgress = new ProgressDialog(mContext);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        /**
        mainbottomNav = findViewById(R.id.bottomNavigation);
        BottomNavigationViewHelper.disableShiftMode(mainbottomNav);
        Menu menu = mainbottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_action_home:
                        Intent homeIntent = new Intent(PostActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        return true;

                    case R.id.bottom_action_add:

                        return true;

                    case R.id.bottom_action_account:
                        Intent accountIntent = new Intent(PostActivity.this, ProfileSetupActivity.class);
                        startActivity(accountIntent);
                        return true;

                    default:
                        return false;


                }
            }
        });
**/
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(PostActivity.this);
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startposting();
            }
        });

        mPlaceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    requestPermission();
                    Intent intent = builder.build(PostActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void startposting() {

        mProgress.setMessage("Posting to Blog......");
        mProgress.setCancelable(false);

        final String desc_val = mPostDesc.getText().toString().trim();
        final String place_val = mPlaceName.getText().toString().trim();
        final String address_val = mAddress.getText().toString().trim();


        if (postImageUri != null &&!TextUtils.isEmpty(place_val) && !TextUtils.isEmpty(desc_val)
                && !TextUtils.isEmpty(address_val)){
            mProgress.show();

            final String randomName = UUID.randomUUID().toString();

            StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
            filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                    final String downloadUri = task.getResult().getDownloadUrl().toString();

                    if(task.isSuccessful()){

                        File newImageFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(PostActivity.this)
                                    .setMaxHeight(200)
                                    .setMaxWidth(200)
                                    .setQuality(2)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                .child(randomName + ".jpg").putBytes(thumbData);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("image_url", downloadUri);
                                postMap.put("image_thumb", downloadthumbUri);
                                postMap.put("desc", desc_val);
                                postMap.put("place_name", place_val);
                                postMap.put("address", address_val);
                                postMap.put("user_id", current_user_id);
                                postMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if(task.isSuccessful()){

                                            Toast.makeText(PostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                            Intent homeIntent = new Intent(PostActivity.this, HomeActivity.class);
                                            startActivity(homeIntent);
                                            finish();

                                        } else {
                                            Toast.makeText(mContext,"Fill Up All Field",Toast.LENGTH_LONG).show();

                                        }
                                        mProgress.dismiss();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //Error handling

                            }
                        });


                    } else {

                    }

                }
            });


        }

    }

    private void initViews() {
        mPlaceIcon = findViewById(R.id.postPlaceIcon);
        mSelectImage = findViewById(R.id.selectPostImage);
        mPostDesc = findViewById(R.id.descPostField);
        mSubmitBtn = findViewById(R.id.submitBtn);
        mToolBar = findViewById(R.id.postToolBar);
        mPlaceName = findViewById(R.id.postPlaceName);
        mAddress = findViewById(R.id.postPlaceAddress);

    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                mSelectImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        } if (requestCode == PLACE_PICKER_REQUEST ) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(PostActivity.this, data);
                //String placeName = String.format("%s",place.getName());
                mPlaceName.setText(place.getName());
                //String placeAddress = String.format("%s",place.getAddress());
                mAddress.setText(place.getAddress());
            }
        }

    }


}