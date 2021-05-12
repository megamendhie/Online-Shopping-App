package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.Query;

import java.util.List;

import adapters.CategoryAdapter;
import adapters.ProductAdapter;
import interfaces.CartOperationListener;
import models.CartProduct;
import models.Category;
import models.Product;
import roomdb.DeSantosViewModel;
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
import static models.Commons.VISIBLE;

public class ProductActivity extends AppCompatActivity implements CartOperationListener {
    private ActionBar actionBar;
    private RecyclerView lstProducts;
    private Category category;
    private DeSantosViewModel viewModel;
    private List<CartProduct> allCartProducts;


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

        viewModel = new ViewModelProvider(this).get(DeSantosViewModel.class);
        viewModel.getCartProducts().observe(this, cartProducts -> allCartProducts = cartProducts);
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
        Query query = FirebaseUtil.getDatabase().collection(COLLECTION).orderBy(CREATED_AT)
                .whereEqualTo(CATEGORY_ID, category.getDocId()).whereEqualTo(VISIBLE, true);
        ProductAdapter adapter = new ProductAdapter(query, this);
        lstProducts.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    public void onAdd(Product product) {
        CartProduct cartProduct = new CartProduct(product);
        if (allCartProducts.contains(cartProduct))
            Snackbar.make(lstProducts, "Already added", Snackbar.LENGTH_SHORT).show();
        else {
            viewModel.addToCart(cartProduct);
            Snackbar.make(lstProducts, "Added to cart", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemove(CartProduct cartProduct) {

    }

    @Override
    public void increaseQuantity(CartProduct product) {

    }

    @Override
    public void deceaseQuantity(CartProduct product) {

    }
}
