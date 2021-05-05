package fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sqube.desantosdirectory.ProductPurchaseActivity;
import com.sqube.desantosdirectory.R;

import java.util.ArrayList;
import java.util.List;

import adapters.CartAdapter;
import interfaces.CartOperationListener;
import models.CartProduct;
import models.Product;
import roomdb.DeSantosViewModel;
import utils.Reusable;

import static models.Commons.PRODUCTS;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment implements CartOperationListener {
    private DeSantosViewModel viewModel;
    private List<CartProduct> allCartProducts;
    private TextView txtPrice;
    private LinearLayout lnrTotalAmount;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        txtPrice = view.findViewById(R.id.txtPrice);
        lnrTotalAmount = view.findViewById(R.id.lnrTotalAmount);
        lnrTotalAmount.setVisibility(View.GONE);
        Button btnPurchase = view.findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(v -> purchase());
        btnPurchase.setVisibility(View.GONE);
        RecyclerView lstCart = view.findViewById(R.id.lstCart);
        lstCart.setLayoutManager(new LinearLayoutManager(getContext()));
        CartAdapter adapter = new CartAdapter(this);
        lstCart.setAdapter(adapter);

        viewModel = new ViewModelProvider(getActivity()).get(DeSantosViewModel.class);
        viewModel.getCartProducts().observe(getActivity(), cartProducts -> {
            allCartProducts = cartProducts;
            adapter.setProducts(allCartProducts);
            btnPurchase.setVisibility(allCartProducts==null||allCartProducts.isEmpty()?View.GONE:View.VISIBLE);
            setTotalPrice();
        });
        return view;
    }

    private void purchase() {
        Intent intent = new Intent(getContext(), ProductPurchaseActivity.class);
        intent.putParcelableArrayListExtra(PRODUCTS, new ArrayList<>(allCartProducts));
        startActivity(intent);
    }

    private void setTotalPrice() {
        if(allCartProducts==null||allCartProducts.isEmpty()) {
         lnrTotalAmount.setVisibility(View.GONE);
         return;
        }
        lnrTotalAmount.setVisibility(View.VISIBLE);
        long totalAmount = 0;
        for (CartProduct product:allCartProducts){
            totalAmount+=(product.getPrice()*product.getQuantity());
        }
        txtPrice.setText(Html.fromHtml("&#8358;"+ Reusable.getFormattedAmount(totalAmount)));
    }

    @Override
    public void onAdd(Product product) {
    }

    @Override
    public void onRemove(CartProduct cartProduct) {
        if(allCartProducts.contains(cartProduct))
            viewModel.deleteFromCart(cartProduct.getDocId());
    }

    @Override
    public void increaseQuantity(CartProduct product) {
        int quantity = product.getQuantity() + 1;
        product.setQuantity(quantity);
        if(quantity>=9999)
            return;
        viewModel.updateCart(product);
    }

    @Override
    public void deceaseQuantity(CartProduct product) {
        if(product.getQuantity()<=1)
            return;
        int quantity = product.getQuantity() - 1;
        product.setQuantity(quantity);
        viewModel.updateCart(product);
    }
}
