package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.Query;

import adapters.ProductAdapter;
import adapters.ProductAdapterAdmin;
import models.Category;
import utils.FirebaseUtil;

import static models.Commons.CATEGORY_ID;
import static models.Commons.CREATED_AT;
import static models.Commons.MODEL;
import static models.Commons.PRODUCTS;
import static models.Commons.SERVICE;
import static models.Commons.SERVICES;

public class AdminProductActivity extends AppCompatActivity {
    private RecyclerView lstProducts;
    private Category category;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button btnAdd = findViewById(R.id.btnAdd);
        lstProducts = findViewById(R.id.lstProduct);

        if(getIntent()==null){
            Toast.makeText(this, "Can't retrieve items", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        category = getIntent().getParcelableExtra(MODEL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        lstProducts.setLayoutManager(gridLayoutManager);
        loadProductCategories();
    }

    private void loadProductCategories() {
        if(category==null)
            return;
        actionBar.setTitle(category.getName());
        String COLLECTION = category.getType().equals(SERVICE)? SERVICES : PRODUCTS;
        Query query = FirebaseUtil.getDatabase().collection(COLLECTION).orderBy(CREATED_AT).whereEqualTo(CATEGORY_ID, category.getDocId());
        ProductAdapterAdmin adapter = new ProductAdapterAdmin(query, this);
        lstProducts.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}