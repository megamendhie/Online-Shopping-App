package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.Query;

import adapters.CategoryAdapter;
import adapters.ProductAdapter;
import models.Category;
import utils.FirebaseUtil;

import static models.Commons.CATEGORIES;
import static models.Commons.CATEGORY_ID;
import static models.Commons.CREATED_AT;
import static models.Commons.MODEL;
import static models.Commons.PRODUCT;
import static models.Commons.PRODUCTS;
import static models.Commons.SERVICE;
import static models.Commons.SERVICES;
import static models.Commons.TYPE;

public class ProductActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private RecyclerView lstProducts;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(getIntent()==null){
            Toast.makeText(this, "Can't retrieve item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        category = getIntent().getParcelableExtra(MODEL);

        lstProducts = findViewById(R.id.lstProduct);
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
        ProductAdapter adapter = new ProductAdapter(query);
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
