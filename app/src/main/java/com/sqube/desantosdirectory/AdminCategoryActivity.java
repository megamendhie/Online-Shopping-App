package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.Query;

import adapters.CategoryAdapter;
import adapters.CategoryAdapterAdmin;
import utils.FirebaseUtil;

import static models.Commons.CATEGORIES;
import static models.Commons.CATEGORY;
import static models.Commons.CREATED_AT;
import static models.Commons.PRODUCT;
import static models.Commons.TYPE;

public class AdminCategoryActivity extends AppCompatActivity {
    private RecyclerView lstCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button btnAdd = findViewById(R.id.btnAdd);
        lstCategory = findViewById(R.id.lstCategory);
        lstCategory.setLayoutManager(new LinearLayoutManager(this));

        if(getIntent()==null){
            Toast.makeText(this, "Can't retrieve items", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String type = getIntent().getStringExtra(CATEGORY);
        actionBar.setTitle(type.equals(PRODUCT)? "Products":"Services");
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            intent.putExtra(TYPE, type);
            startActivity(intent);
        });

        loadCategories(type);
    }

    private void loadCategories(String type) {
        Query query = FirebaseUtil.getDatabase().collection(CATEGORIES).orderBy(CREATED_AT).whereEqualTo(TYPE, type);
        CategoryAdapterAdmin adapter = new CategoryAdapterAdmin(query);
        lstCategory.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}