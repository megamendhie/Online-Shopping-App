package adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.R;

import models.Product;
import utils.Reusable;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductHolder> {

    public ProductAdapter(@NonNull Query query) {
        super(new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build());
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
        holder.txtName.setText(model.getName());
        holder.txtSize.setText(model.getSize());
        holder.txtPrice.setText(Html.fromHtml("&#8358;"+Reusable.getFormattedAmount(model.getPrice())));
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductHolder(view);
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        RelativeLayout lnrProduct;
        ImageView imgIcon, imgAdd;
        TextView txtName, txtSize, txtPrice;

        ProductHolder(@NonNull View itemView) {
            super(itemView);
            lnrProduct = itemView.findViewById(R.id.lnrProduct);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            txtName = itemView.findViewById(R.id.txtName);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
