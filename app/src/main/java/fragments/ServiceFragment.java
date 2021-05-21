package fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.R;
import com.sqube.desantosdirectory.SearchActivity;

import adapters.CategoryAdapter;
import utils.FirebaseUtil;

import static models.Commons.CATEGORIES;
import static models.Commons.CREATED_AT;
import static models.Commons.PRODUCT;
import static models.Commons.SERVICE;
import static models.Commons.SERVICES;
import static models.Commons.TYPE;
import static models.Commons.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {
    private RecyclerView lstServices;
    private CategoryAdapter adapter;

    public ServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        lstServices = view.findViewById(R.id.lstCategory);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        lstServices.setLayoutManager(gridLayoutManager);
        CardView crdSearch = view.findViewById(R.id.search_cardView);
        crdSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra(TYPE,SERVICES);
            startActivity(intent);
        });
        loadProductCategories();
        return view;
    }

    private void loadProductCategories() {
        Query query = FirebaseUtil.getDatabase().collection(CATEGORIES).orderBy(CREATED_AT)
                .whereEqualTo(TYPE, SERVICE).whereEqualTo(VISIBLE, true);
        adapter = new CategoryAdapter(query);
        lstServices.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
}
