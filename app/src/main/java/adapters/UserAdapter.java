package adapters;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.sqube.desantosdirectory.R;
import com.sqube.desantosdirectory.ServiceRequestActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import utils.FirebaseUtil;

import static models.Commons.USERS;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {

    public UserAdapter(@NonNull Query query) {
        super(new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build());
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.txtName.setText(String.format("%s %s", model.getFirstName(), model.getLastName()));
        holder.txtEmail.setText(model.getEmail());

        if(model.getPic()!=null && !model.getPic().isEmpty() && !model.getPic().equals("non"))
            Glide.with(holder.imgDp.getContext()).load(model.getPic()).into(holder.imgDp);
        if(model.isAdmin()){
            holder.txtAdmin.setText("Admin");
            holder.txtRole.setText(model.getRole().equals("owner")? "owner": "moderator");
            holder.txtRole.setVisibility(View.VISIBLE);
        }
        else{
            holder.txtAdmin.setText("User");
            holder.txtRole.setVisibility(View.GONE);
        }

        holder.imgMore.setOnClickListener(v -> showPrompt(holder.imgMore, model));

    }

    private void showPrompt(View imgMore, User model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(imgMore.getContext());
        LayoutInflater inflater = LayoutInflater.from(imgMore.getContext());
        View dialogView = inflater.inflate(R.layout.dialog_user, null);
        builder.setView(dialogView);
        final AlertDialog dialog= builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        LinearLayout lnrRole = dialog.findViewById(R.id.lnrRole);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        RadioButton rbYes = dialog.findViewById(R.id.rbYes);
        RadioButton rbNo = dialog.findViewById(R.id.rbNo);
        RadioButton rbOwner = dialog.findViewById(R.id.rbOwner);
        RadioButton rbModerator = dialog.findViewById(R.id.rbModerator);
        RadioGroup rgAdmin = dialog.findViewById(R.id.rgAdmin);
        RadioGroup rgRole = dialog.findViewById(R.id.rgRole);
        final boolean[] isAdmin = {model.isAdmin()};
        if(model.isAdmin()){
            rbYes.setChecked(true);
            if(model.getRole().equals("owner"))
                rbOwner.setChecked(true);
            else
                rbModerator.setChecked(true);
        }
        else{
            rbNo.setChecked(true);
            lnrRole.setVisibility(View.GONE);
        }
        rgAdmin.setOnCheckedChangeListener((group, checkedId) -> {
           if (group.getCheckedRadioButtonId()==R.id.rbYes){
               isAdmin[0] = true;
               lnrRole.setVisibility(View.VISIBLE);
            }
           else {
               isAdmin[0] = false;
               lnrRole.setVisibility(View.GONE);
           }
        });

        btnUpdate.setOnClickListener(v -> {
            model.setAdmin(isAdmin[0]);
            if(isAdmin[0]){
                if(rbOwner.isChecked())
                    model.setRole("owner");
                else
                    model.setRole("user");
            }
            else
                model.setRole("user");
            FirebaseUtil.getDatabase().collection(USERS).document(model.getUid()).set(model, SetOptions.merge());
            dialog.dismiss();

        });

    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserHolder(view);
    }

    class UserHolder extends RecyclerView.ViewHolder {
        CircleImageView imgDp;
        ImageView imgMore;
        TextView txtName, txtEmail, txtAdmin, txtRole;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            imgDp = itemView.findViewById(R.id.imgDp);
            imgMore = itemView.findViewById(R.id.imgMore);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtAdmin = itemView.findViewById(R.id.txtAdmin);
            txtRole = itemView.findViewById(R.id.txtRole);
        }
    }
}
