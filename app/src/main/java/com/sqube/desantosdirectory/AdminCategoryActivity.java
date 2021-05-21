package com.sqube.desantosdirectory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.Query;
import com.theartofdev.edmodo.cropper.CropImage;

import adapters.CategoryAdapterAdmin;
import interfaces.OnAddListener;
import modals.AddCategoryFragment;
import models.Category;
import models.Product;
import utils.FirebaseUtil;

import static models.Commons.CATEGORIES;
import static models.Commons.CATEGORY;
import static models.Commons.CREATED_AT;
import static models.Commons.PRODUCT;
import static models.Commons.TYPE;

public class AdminCategoryActivity extends AppCompatActivity implements OnAddListener {
    private RecyclerView lstCategory;
    private AddCategoryFragment addCategoryFragment;

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
        actionBar.setTitle(type.equals(PRODUCT)? "Listings":"Services");
        btnAdd.setOnClickListener(v -> showAddCategoryDialog(type));
        loadCategories(type);
    }

    private void loadCategories(String type) {
        Query query = FirebaseUtil.getDatabase().collection(CATEGORIES).orderBy(CREATED_AT).whereEqualTo(TYPE, type);
        CategoryAdapterAdmin adapter = new CategoryAdapterAdmin(query, this);
        lstCategory.setAdapter(adapter);
        adapter.startListening();
    }


    private void showAddCategoryDialog(String type) {
        addCategoryFragment = new AddCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("EDITABLE", false);
        bundle.putString(TYPE, type);
        addCategoryFragment.setArguments(bundle);
        addCategoryFragment.show(getSupportFragmentManager(), "AddCategoryBottomSheet");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE )
            addCategoryFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onComplete(boolean editable) {
        addCategoryFragment.dismiss();
        Snackbar.make(lstCategory, editable?"UPDATED":"ADDED", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onEdit(Category category, boolean delete) {
        if (delete) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            builder.setMessage("Are you sure you want to delete "+category.getName()+" category?")
                    .setTitle("Delete Category")
                    .setPositiveButton("Proceed", (dialogInterface, i) -> deleteCategory(category))
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                    .show();
        } else {
            addCategoryFragment = new AddCategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("EDITABLE", true);
            bundle.putParcelable(CATEGORY, category);
            addCategoryFragment.setArguments(bundle);
            addCategoryFragment.show(getSupportFragmentManager(), "AddCategoryBottomSheet");
        }
    }

    private void deleteCategory(Category category) {
        FirebaseUtil.getDatabase().collection(CATEGORIES).document(category.getDocId()).delete()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                        Snackbar.make(lstCategory, "DELETED", Snackbar.LENGTH_SHORT).show();
                });
    }
}