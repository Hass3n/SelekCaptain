package minya.salek.salekcaptain;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import minya.salek.salekcaptain.Model.OldTripModel;
import minya.salek.salekcaptain.Model.UserModel;

public class NewTripAdapter extends RecyclerView.Adapter<NewTripAdapter.Holder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    ArrayList<TripModel> userTrip;

    public NewTripAdapter(Context context, ArrayList<TripModel> userTrip) {
        this.context = context;
        this.userTrip = userTrip;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_new_trip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        TripModel currentUserTrip = userTrip.get(position);

        holder.tvName.setText(currentUserTrip.getUname());
        holder.showRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog(currentUserTrip);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userTrip.size();
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button showRequest;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.new_user_name_txt);
            showRequest = itemView.findViewById(R.id.show_request_btn);


        }
    }

    TextView phone, location, destination, price;
    ProgressBar progressBar;

    void showCustomDialog(TripModel currentUserTrip) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        View mView = LayoutInflater.from(context).inflate(R.layout.captain_notification_dialog, null);
        final Button acceptBtn, cancelBtn;
        final ImageView userIcon, callIcon;


        phone = mView.findViewById(R.id.notification_phone);
        location = mView.findViewById(R.id.current_location_txt);
        destination = mView.findViewById(R.id.destination_location_txt);
        price = mView.findViewById(R.id.notification_price_text);
        acceptBtn = mView.findViewById(R.id.notification_accept_btn);
        cancelBtn = mView.findViewById(R.id.notification_cancel_btn);
        callIcon = mView.findViewById(R.id.call_icon);
        userIcon = mView.findViewById(R.id.user_icon);
        progressBar = mView.findViewById(R.id.captain_accept_trip_load);


        phone.setText(currentUserTrip.getPhone());
        location.setText(currentUserTrip.getStrip());
        destination.setText(currentUserTrip.getEndTrip());
        price.setText(currentUserTrip.getTripPrice());
        Glide.with(context).load(currentUserTrip.getImage())
                .placeholder(R.drawable.mask_group).into(userIcon);


        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAtRuntime(phone.getText().toString());
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrips(currentUserTrip,dialog);
                Prevalent.currentTripModel = currentUserTrip;
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(mView);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void updateTrips(TripModel currentUserTrip, Dialog dialog){
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
        pDialog.setTitleText("جاري التحميل");
        pDialog.setCancelable(false);
        pDialog.show();
        DatabaseReference request;
        request = FirebaseDatabase.getInstance().getReference();
        request.child("Trips").child(currentUserTrip.captainId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> updates = new HashMap<String,Object>();
                updates.put("IsAccept","Yes");
                request.child("Trips").child(currentUserTrip.captainId).updateChildren(updates);

                pDialog.dismiss();
                dialog.dismiss();
                context.startActivity(new Intent(context,StartTripActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callAtRuntime(String phone) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context.checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));
            context.startActivity(intent);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callAtRuntime(phone.getText().toString());
            } else {
                Toast.makeText(context, "Permission Denied.Try Again !", Toast.LENGTH_SHORT).
                        show();
            }
        }
    }
}