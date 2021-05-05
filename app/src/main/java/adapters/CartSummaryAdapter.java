package adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sqube.desantosdirectory.R;

import java.util.ArrayList;

import models.CartProduct;
import utils.Reusable;

public class CartSummaryAdapter extends RecyclerView.Adapter<CartSummaryAdapter.CartSummaryHolder> {
    ArrayList<CartProduct> products;

    public CartSummaryAdapter(ArrayList<CartProduct> products){
        this.products = products;
    }

    @NonNull
    @Override
    public CartSummaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_summary, parent, false);
        return new CartSummaryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartSummaryHolder holder, int position) {
        holder.bindViews(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class CartSummaryHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice;
        public CartSummaryHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }

        public void bindViews(CartProduct product){
            txtName.setText(product.getName());
            txtPrice.setText(Html.fromHtml("&#8358;"+ Reusable.getFormattedAmount(product.getPrice()*product.getQuantity())));
        }
    }
}
