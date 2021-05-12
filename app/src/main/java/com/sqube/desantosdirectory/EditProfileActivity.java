package com.sqube.desantosdirectory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import utils.FirebaseUtil;

import static models.Commons.PROFILE_PICTURES;
import static models.Commons.USERS;

public class EditProfileActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    private User myProfile;
    private Button btnUpdate;
    private EditText edtFName, edtLName, edtAddress, edtPhone;
    private CircleImageView imgDp;
    private ProgressBar prgUpdate;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edit Profile");
        btnUpdate = findViewById(R.id.btnUpdate); btnUpdate.setOnClickListener(v -> updateDetails());
        edtFName = findViewById(R.id.edtFname);
        edtLName = findViewById(R.id.edtLname);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        imgDp = findViewById(R.id.imgDp); imgDp.setOnClickListener(v -> openCropper());
        ImageView imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(v -> openCropper());
        prgUpdate = findViewById(R.id.prgUpdate);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = prefs.getString("profile", "");
        myProfile = (json.equals("")) ? null : gson.fromJson(json, User.class);

        edtFName.setText(myProfile.getFirstName());
        edtLName.setText(myProfile.getLastName());
        edtAddress.setText(myProfile.getAddress());
        edtPhone.setText(myProfile.getPhone());
        if(myProfile.getPic()!=null && !myProfile.getPic().isEmpty() && !myProfile.getPic().equals("non"))
            Glide.with(this).load(myProfile.getPic()).into(imgDp);
    }


    private void openCropper() {
        CropImage.activity().setAspectRatio(1,1).start(this);
    }

    private void updateDetails(){
        String fName = edtFName.getText().toString();
        String lName = edtLName.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();


        if(fName.isEmpty()||fName.length()<3){
            edtFName.setError("Enter First Name");
            return;
        }
        if(lName.isEmpty()||lName.length()<3){
            edtLName.setError("Enter Last Name");
            return;
        }

        myProfile.setFirstName(fName);
        myProfile.setLastName(lName);
        myProfile.setPhone(phone);
        myProfile.setAddress(address);

        prgUpdate.setVisibility(View.VISIBLE);
        btnUpdate.setEnabled(false);
        if(filePath!=null)
            FirebaseUtil.getStorage().getReference().child(PROFILE_PICTURES).child(myProfile.getUid()).putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                myProfile.setPic(url);
                                FirebaseUtil.getDatabase().collection(USERS).document(myProfile.getUid()).set(myProfile, SetOptions.merge());
                                prgUpdate.setVisibility(View.GONE);
                                btnUpdate.setEnabled(true);
                                Snackbar.make(btnUpdate, "UPDATED", Snackbar.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> {
                        prgUpdate.setVisibility(View.GONE);
                        btnUpdate.setEnabled(true);
                        Toast.makeText(this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                    });
        else
            FirebaseUtil.getDatabase().collection(USERS).document(myProfile.getUid()).set(myProfile, SetOptions.merge())
                    .addOnCompleteListener(task -> {
                prgUpdate.setVisibility(View.GONE);
                btnUpdate.setEnabled(true);
                if(task.isSuccessful())
                    Snackbar.make(btnUpdate, "UPDATED", Snackbar.LENGTH_SHORT).show();
                else
                    Toast.makeText(EditProfileActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                Glide.with(this).load(filePath).into(imgDp);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Failed. "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}