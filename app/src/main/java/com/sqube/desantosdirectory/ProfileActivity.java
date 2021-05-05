package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import models.User;
import utils.FirebaseUtil;

import static models.Commons.CATEGORY;
import static models.Commons.PRODUCT;
import static models.Commons.SERVICE;

public class ProfileActivity extends AppCompatActivity{
    private TextView txtName;
    private String userId;
    private final Gson gson = new Gson();
    private User myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = prefs.getString("profile", "");
        myProfile = (json.equals("")) ? null : gson.fromJson(json, User.class);
        if(myProfile==null)
            return;

        setContentView(myProfile.isAdmin()?R.layout.activity_profile:R.layout.activity_profile_user);
        if(myProfile.isAdmin()){
            Button btnManageAdmin = findViewById(R.id.btnManageAdmin);
            btnManageAdmin.setVisibility(myProfile.getRole().equals("owner")?View.VISIBLE:View.GONE);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile");

        txtName = findViewById(R.id.txtName);
        if(FirebaseUtil.getAuth().getCurrentUser()==null)
            return;
        userId = FirebaseUtil.getAuth().getCurrentUser().getUid();

        ImageView imgDp = findViewById(R.id.imgDp);

        txtName.setText(String.format("%s %s", myProfile.getFirstName(), myProfile.getLastName()));
        if(myProfile.getPic()!=null&&!myProfile.getPic().isEmpty()&&!myProfile.getPic().equals("non"))
            Glide.with(imgDp.getContext()).load(myProfile.getPic()).into(imgDp);
    }

    public void openProductOrders(View view){
        startActivity(new Intent(this, ProductPurchaseAdminActivity.class));
    }

    public void openServiceRequests(View view){
        startActivity(new Intent(this, ServiceRequestAdminActivity.class));
    }

    public void openCategory(View view) {
        String category = view.getId()==R.id.btnViewProducts?PRODUCT:SERVICE;
        Intent intent = new Intent(this, AdminCategoryActivity.class);
        intent.putExtra(CATEGORY, category);
        startActivity(intent);
    }

    public void signOut(View view) {
        if (FirebaseUtil.getAuth().getCurrentUser() != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient= GoogleSignIn.getClient(ProfileActivity.this, gso);

            FirebaseUtil.getAuth().signOut();
            mGoogleSignInClient.signOut();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}