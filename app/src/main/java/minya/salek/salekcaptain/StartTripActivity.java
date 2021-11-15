package minya.salek.salekcaptain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StartTripActivity extends AppCompatActivity {

    private Button startTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startTrip = findViewById(R.id.start_trip_btn);

        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog pDialog = new SweetAlertDialog(StartTripActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
                pDialog.setTitleText("جاري التحميل");
                pDialog.setCancelable(false);
                pDialog.show();
                FirebaseDatabase.getInstance()
                        .getReference("captainlocation")
                        .child(Prevalent.currentTripModel.getCaptainId())
                        .removeValue();

                pDialog.dismiss();
                startActivity(new Intent(StartTripActivity.this,EndTripActivity.class));
            }
        });
    }
}
