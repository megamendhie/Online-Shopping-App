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
import android.widget.ImageView;

import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.LoginActivity;
import com.sqube.desantosdirectory.ProfileActivity;
import com.sqube.desantosdirectory.R;
import com.sqube.desantosdirectory.SearchActivity;

import adapters.CategoryAdapter;
import utils.FirebaseUtil;

import static models.Commons.CATEGORIES;
import static models.Commons.CREATED_AT;
import static models.Commons.PRODUCT;
import static models.Commons.PRODUCTS;
import static models.Commons.SERVICE;
import static models.Commons.TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment {
    private RecyclerView lstProducts;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        lstProducts = view.findViewById(R.id.lstCategory);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        lstProducts.setLayoutManager(gridLayoutManager);
        ImageView imgHome = view.findViewById(R.id.imgHome);
        imgHome.setOnLongClickListener(v -> {
            if(FirebaseUtil.getAuth().getCurrentUser()==null)
                startActivity(new Intent(getContext(), LoginActivity.class));
            else
                startActivity(new Intent(getContext(), ProfileActivity.class));
            return false;
        });
        CardView crdSearch = view.findViewById(R.id.search_cardView);
        crdSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra(TYPE,PRODUCTS);
            startActivity(intent);
        });
        loadProductCategories();
        return view;
    }

    private void loadProductCategories() {
        Query query = FirebaseUtil.getDatabase().collection(CATEGORIES).orderBy(CREATED_AT).whereEqualTo(TYPE, PRODUCT);
        CategoryAdapter adapter = new CategoryAdapter(query);
        lstProducts.setAdapter(adapter);
        adapter.startListening();
    }

}
