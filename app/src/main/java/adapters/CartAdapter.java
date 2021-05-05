package adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sqube.desantosdirectory.R;

import java.util.List;

import interfaces.CartOperationListener;
import models.CartProduct;
import roomdb.DeSantosViewModel;
import utils.Reusable;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private List<CartProduct> products;
    private CartOperationListener listener;

    public CartAdapter(Context context){
        listener = (CartOperationListener) context;
    }
    public CartAdapter(Fragment fragment){
        listener = (CartOperationListener) fragment;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        CartProduct product = products.get(position);
        holder.bindView(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<CartProduct> products) {
        this.products = products;
        this.notifyDataSetChanged();
    }

    class CartHolder extends RecyclerView.ViewHolder {
        ImageView imgAdd, imgSubtract, imgRemove, imgIcon;
        TextView txtName, txtSize, txtQuantity, txtPrice;
        public CartHolder(@NonNull View itemView) {
            super(itemView);

            imgAdd = itemView.findViewById(R.id.imgAdd);
            imgSubtract = itemView.findViewById(R.id.imgSubtract);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
        }

        public void bindView(CartProduct model){
            txtName.setText(model.getName());
            String pricePerSize = String.format("%s, &#8358;%s", model.getSize(), Reusable.getFormattedAmount(model.getPrice()));
            txtSize.setText(Html.fromHtml(pricePerSize));
            long totalAmount = model.getPrice()*model.getQuantity();
            txtPrice.setText(Html.fromHtml("&#8358;"+ Reusable.getFormattedAmount(totalAmount)));
            txtQuantity.setText(String.valueOf(model.getQuantity()));

            if(model.getIcon()!=null && !model.getIcon().isEmpty() && !model.equals("non"))
                Glide.with(imgIcon.getContext()).load(model.getIcon()).into(imgIcon);

            imgRemove.setOnClickListener(v -> listener.onRemove(model));
            imgSubtract.setOnClickListener(v -> listener.deceaseQuantity(model));
            imgAdd.setOnClickListener(v -> listener.increaseQuantity(model));
        }
    }
}
