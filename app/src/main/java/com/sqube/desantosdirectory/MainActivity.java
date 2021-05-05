package com.sqube.desantosdirectory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fragments.CartFragment;
import fragments.ProductFragment;
import fragments.ServiceFragment;
import services.UserDataFetcher;
import utils.FirebaseUtil;

import static models.Commons.FRAG_CART;
import static models.Commons.FRAG_PRODUCT;
import static models.Commons.FRAG_SERVICE;
import static models.Commons.LAST_FRAGMENT;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    final Fragment fragmentProduct  = new ProductFragment();
    final Fragment fragmentService = new ServiceFragment();
    final Fragment fragmentCart = new CartFragment();

    private Fragment fragmentActive = fragmentProduct;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navBottom = findViewById(R.id.bottom_navigation);
        navBottom.setOnNavigationItemSelectedListener(this);

        if(savedInstanceState!=null){
            String tag = savedInstanceState.getString(LAST_FRAGMENT);
            switch (tag){
                case FRAG_PRODUCT:
                    getSupportFragmentManager().beginTransaction().hide(fragmentActive).show(fragmentProduct).commit();
                    fragmentActive = fragmentProduct;
                    break;
                case FRAG_SERVICE:
                    getSupportFragmentManager().beginTransaction().hide(fragmentActive).show(fragmentService).commit();
                    fragmentActive = fragmentService;
                    break;
                case FRAG_CART:
                    getSupportFragmentManager().beginTransaction().hide(fragmentActive).show(fragmentCart).commit();
                    fragmentActive = fragmentCart;
                    break;
            }
        }
        else
            loadFragment();

        serviceIntent = new Intent(MainActivity.this, UserDataFetcher.class);
        startService(serviceIntent);
    }

    private void loadFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container,fragmentCart, FRAG_CART).hide(fragmentCart);
        fragmentTransaction.add(R.id.main_container,fragmentService, FRAG_SERVICE).hide(fragmentService);
        fragmentTransaction.add(R.id.main_container,fragmentProduct, FRAG_PRODUCT).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                if (fragmentActive != fragmentProduct) {
                    fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentProduct).commit();
                    fragmentActive = fragmentProduct;
                }
                return true;
            case R.id.nav_favorite:
                if (fragmentActive != fragmentService) {
                    fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentService).commit();
                    fragmentActive = fragmentService;
                }
                return true;
            case R.id.nav_history:
                if (fragmentActive != fragmentCart) {
                    fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentCart).commit();
                    fragmentActive = fragmentCart;
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(LAST_FRAGMENT, fragmentActive.getTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceIntent!=null)
            stopService(serviceIntent);
    }
}
