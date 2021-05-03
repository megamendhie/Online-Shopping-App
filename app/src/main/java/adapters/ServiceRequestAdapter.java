package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sqube.desantosdirectory.AddCategoryActivity;
import com.sqube.desantosdirectory.R;

import models.ServiceRequest;

public class ServiceRequestAdapter extends FirestoreRecyclerAdapter<ServiceRequest, ServiceRequestAdapter.ServiceRequestHolder> {
    private final Context context;

    public ServiceRequestAdapter(@NonNull Query query, Context context) {
        super(new FirestoreRecyclerOptions.Builder<ServiceRequest>()
                .setQuery(query, ServiceRequest.class)
                .build());
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ServiceRequestHolder holder, int position, @NonNull ServiceRequest model) {
        String date = DateFormat.format("d MMM", model.getCreatedAt()).toString();
        holder.txtDate.setText(date);
        holder.txtName.setText(String.format("%s %s", model.getfName(), model.getlName()));
        holder.txtServiceName.setText(model.getServiceName());

        switch (model.getStatus()){
            case 0:
                ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(context.getResources()
                        .getColor(R.color.colorStatusAmber)));
                break;
            case 1:
                ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(context.getResources()
                        .getColor(R.color.colorPrimary)));
                break;
            case 2:
                ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(context.getResources()
                        .getColor(R.color.colorStatusRed)));
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), AddCategoryActivity.class));
            }
        });

        Glide.with(context).load(model.getIcon()).into(holder.imgIcon);

    }

    @NonNull
    @Override
    public ServiceRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_service, parent, false);
        return new ServiceRequestHolder(view);
    }

    class ServiceRequestHolder extends RecyclerView.ViewHolder {
        ImageView imgStatus, imgIcon;
        TextView txtServiceName, txtName, txtDate;
        public ServiceRequestHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
