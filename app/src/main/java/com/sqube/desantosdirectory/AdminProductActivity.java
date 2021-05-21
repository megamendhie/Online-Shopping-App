package com.sqube.desantosdirectory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.Query;
import com.theartofdev.edmodo.cropper.CropImage;

import adapters.ProductAdapterAdmin;
import interfaces.OnAddProductListener;
import modals.AddProductFragment;
import models.Category;
import models.Product;
import utils.FirebaseUtil;

import static models.Commons.CATEGORY_ID;
import static models.Commons.CREATED_AT;
import static models.Commons.MODEL;
import static models.Commons.PRODUCT;
import static models.Commons.PRODUCTS;
import static models.Commons.PRODUCT_ID;

public class AdminProductActivity extends AppCompatActivity implements OnAddProductListener {
    private AddProductFragment addProductFragment;
    private RecyclerView lstProducts;
    private Category category;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button btnAdd = findViewById(R.id.btnAdd); btnAdd.setOnClickListener(v -> showAddProductDialog());
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
        Query query = FirebaseUtil.getDatabase().collection(PRODUCTS).orderBy(CREATED_AT).whereEqualTo(CATEGORY_ID, category.getDocId());
        ProductAdapterAdmin adapter = new ProductAdapterAdmin(query, this);
        lstProducts.setAdapter(adapter);
        adapter.startListening();
    }

    private void showAddProductDialog() {
        addProductFragment = new AddProductFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("EDITABLE", false);
        bundle.putString(CATEGORY_ID, category.getDocId());
        addProductFragment.setArguments(bundle);
        addProductFragment.show(getSupportFragmentManager(), "AddProductBottomSheet");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE )
            addProductFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    public void onComplete(boolean editable) {
        addProductFragment.dismiss();
        Snackbar.make(lstProducts, editable?"UPDATED":"ADDED", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onEdit(Product product, boolean delete) {
        if (delete) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            builder.setMessage("Are you sure you want to delete listing?")
                    .setTitle("Delete Listing")
                    .setPositiveButton("Proceed", (dialogInterface, i) -> deleteCategory(product))
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                    .show();
        } else {
            addProductFragment = new AddProductFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("EDITABLE", true);
            bundle.putParcelable(PRODUCT, product);
            bundle.putString(PRODUCT_ID, product.getDocId());
            addProductFragment.setArguments(bundle);
            addProductFragment.show(getSupportFragmentManager(), "AddProductBottomSheet");
        }
    }

    private void deleteCategory(Product product) {
        FirebaseUtil.getDatabase().collection(PRODUCTS).document(product.getDocId()).delete()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                        Snackbar.make(lstProducts, "DELETED", Snackbar.LENGTH_SHORT).show();
                });
    }
}