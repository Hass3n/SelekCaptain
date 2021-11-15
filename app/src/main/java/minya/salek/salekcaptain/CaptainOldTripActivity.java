package minya.salek.salekcaptain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import minya.salek.salekcaptain.Fragment.FragmentAdapter;
import minya.salek.salekcaptain.Model.OldTripModel;

public class CaptainOldTripActivity extends AppCompatActivity {
    private ImageView captainImage;
    private TextView captainName, captainPhone, noOldTrip;

    private TabLayout tabLayout;
    private ViewPager2 pager2;

    private FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_old_trip);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        captainImage = findViewById(R.id.captain_profile_image);
        captainName = findViewById(R.id.captain_old_name_txt);
        captainPhone = findViewById(R.id.captain_old_phone_txt);
        noOldTrip = findViewById(R.id.captain_old_trip_empty);
        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager2);

        captainName.setText(Prevalent.currentOnlineUser.getName());
        captainPhone.setText(Prevalent.currentOnlineUser.getPhone());

        Glide.with(this).load(Prevalent.currentOnlineUser.getImageUrl())
                .placeholder(R.drawable.mask_group).into(captainImage);

        FragmentManager fm = getSupportFragmentManager();

        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);


        tabLayout.addTab(tabLayout.newTab().setText("طلبات الرحلات"));
        tabLayout.addTab(tabLayout.newTab().setText("الرحلات السابقة"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // tabLayout.setTextDirection(Text);
                //tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#5b80c6"));
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }


}
