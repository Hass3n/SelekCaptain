package minya.salek.salekcaptain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EndTripActivity extends AppCompatActivity {

    private Button endTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        endTrip = findViewById(R.id.end_trip_btn);

        endTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripAccepted(Prevalent.currentTripModel);
            }
        });
    }

    private void tripAccepted(TripModel currentUserTrip) {
        //progressBar.setVisibility(View.VISIBLE);
        SweetAlertDialog pDialog = new SweetAlertDialog(EndTripActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
        pDialog.setTitleText("جاري التحميل");
        pDialog.setCancelable(false);
        pDialog.show();
        DatabaseReference request;
        request = FirebaseDatabase.getInstance().getReference();
        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap userMap = new HashMap();
                userMap.put("userId", currentUserTrip.getUserId());
                userMap.put("captainId", currentUserTrip.getCaptainId());
                userMap.put("endTrip", currentUserTrip.getEndTrip());
                userMap.put("tripDate", currentUserTrip.getTripDate());
                userMap.put("tripPrice", currentUserTrip.getTripPrice());
                userMap.put("rate", "false");
                request.child("Accepted Trips").child(currentUserTrip.getUserId()).push().setValue(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    userMap.put("rate", "");
                                    request.child("Captain Accepted Trips").child(currentUserTrip.captainId).push().setValue(userMap);

                                    FirebaseDatabase.getInstance()
                                            .getReference("Trips")
                                            .child(Prevalent.currentTripModel.getCaptainId())
                                            .removeValue();

                                    /*FirebaseDatabase.getInstance()
                                            .getReference("captainlocation")
                                            .child(currentUserTrip.getCaptainId())
                                            .removeValue();*/

                                    //progressBar.setVisibility(View.GONE);
                                    pDialog.dismiss();

                                    /*Toast.makeText(EndTripActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EndTripActivity.this,StartTripActivity.class));
                                    */
                                    startActivity(new Intent(EndTripActivity.this, CaptainProfileActivity.class));
                                    Toast.makeText(EndTripActivity.this, "يجب ان تكون متاح علي سالك لكي تستقبل طبات جديده", Toast.LENGTH_LONG).show();

                                } else {
                                    //progressBar.setVisibility(View.GONE);
                                    pDialog.dismiss();
                                    String message = Objects.requireNonNull(task.getException()).toString();
                                    Toast.makeText(EndTripActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //progressBar.setVisibility(View.GONE);
                        pDialog.dismiss();
                        Toast.makeText(EndTripActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //progressBar.setVisibility(View.GONE);
                pDialog.dismiss();
            }
        });
    }

}
