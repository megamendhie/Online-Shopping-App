package adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sqube.desantosdirectory.R;

import java.util.ArrayList;
import models.CartProduct;
import utils.Reusable;

public class PurchasedProductsAdapter extends RecyclerView.Adapter<PurchasedProductsAdapter.PurchasedProductHolder> {

    private final ArrayList<CartProduct> products;

    public PurchasedProductsAdapter(ArrayList<CartProduct> products){
        this.products = products;
    }

    @NonNull
    @Override
    public PurchasedProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordered_product, parent, false);
        return new PurchasedProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasedProductHolder holder, int position) {
        CartProduct product = products.get(position);
        holder.bindView(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class PurchasedProductHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtName, txtSize, txtQuantity, txtPrice;
        public PurchasedProductHolder(@NonNull View itemView) {
            super(itemView);

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
        }
    }
}
