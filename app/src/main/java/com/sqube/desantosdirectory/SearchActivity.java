package com.sqube.desantosdirectory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.Query;

import adapters.ProductAdapter;
import utils.FirebaseUtil;

import static models.Commons.NAME;
import static models.Commons.PRODUCTS;
import static models.Commons.TYPE;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchList;
    private ProgressBar prgSearch;
    private TextView txtPrompt;
    private SearchView.OnQueryTextListener onQueryTextListener;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchList = findViewById(R.id.searchList);
        prgSearch = findViewById(R.id.prgSearch);
        txtPrompt = findViewById(R.id.txtPrompt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        searchList.setLayoutManager(gridLayoutManager);
        setOnQuery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(SearchActivity.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(onQueryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    private void setOnQuery() {
        onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchTerm) {
                prgSearch.setVisibility(View.VISIBLE);
                txtPrompt.setVisibility(View.GONE);

                String COLLECTION = getIntent()!=null? getIntent().getStringExtra(TYPE): PRODUCTS;

                Query query = FirebaseUtil.getDatabase().collection(COLLECTION).orderBy(NAME)
                        .startAt(searchTerm).endAt(searchTerm + "\uf8ff");
                query.get().addOnCompleteListener(task -> {
                    prgSearch.setVisibility(View.GONE);
                    if(task.isSuccessful() && task.getResult().isEmpty())
                        txtPrompt.setVisibility(View.VISIBLE);

                });
                adapter = new ProductAdapter(query);
                searchList.setAdapter(adapter);
                adapter.startListening();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
