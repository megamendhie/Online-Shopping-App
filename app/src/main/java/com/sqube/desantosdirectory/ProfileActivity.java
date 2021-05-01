package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import models.User;
import utils.FirebaseUtil;

import static models.Commons.CATEGORY;
import static models.Commons.PRODUCT;
import static models.Commons.SERVICE;
import static models.Commons.USERS;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile");

        txtName = findViewById(R.id.txtName);
        Button btnViewProducts = findViewById(R.id.btnViewProducts);
        btnViewProducts.setOnClickListener(this);
        Button btnViewServices = findViewById(R.id.btnViewServices);
        btnViewServices.setOnClickListener(this);
        Button btnOrders = findViewById(R.id.btnOrders);
        btnOrders.setOnClickListener(this);
        Button btnManageAdmin = findViewById(R.id.btnManageAdmin);
        btnManageAdmin.setOnClickListener(this);
        Button btnSignout = findViewById(R.id.btnSignout);
        btnSignout.setOnClickListener(this);
        if(FirebaseUtil.getAuth().getCurrentUser()==null)
            return;
        userId = FirebaseUtil.getAuth().getCurrentUser().getUid();
        setUserDetails();
    }

    private void setUserDetails() {
        FirebaseUtil.getDatabase().collection(USERS).document(userId).get().addOnCompleteListener(this, task -> {
            if(!task.isSuccessful()||task.getResult()==null)
                return;
            User user = task.getResult().toObject(User.class);
            txtName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignout:
                signOut();
                break;
            case R.id.btnViewProducts:
                openCategory(PRODUCT);
                break;
            case R.id.btnViewServices:
                openCategory(SERVICE);
                break;
            case R.id.btnOrders:
            case R.id.btnManageAdmin:
                startActivity(new Intent(this, AddCategoryActivity.class));
                break;
        }

    }

    private void openCategory(String category) {
        Intent intent = new Intent(this, AdminCategoryActivity.class);
        intent.putExtra(CATEGORY, category);
        startActivity(intent);
    }

    private void signOut(){
        FirebaseUtil.getAuth().signOut();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}