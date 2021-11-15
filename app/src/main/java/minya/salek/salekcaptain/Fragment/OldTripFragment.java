package minya.salek.salekcaptain.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import minya.salek.salekcaptain.Model.OldTripModel;
import minya.salek.salekcaptain.OldTripAdapter;
import minya.salek.salekcaptain.R;


public class OldTripFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<OldTripModel> oldTrip;
    private DatabaseReference request;
    private String userId;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView noOldTrip;

    public OldTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_old, container, false);
        recyclerView = view.findViewById(R.id.old_trip_recycler);
        progressBar = view.findViewById(R.id.captain_old_trip_load);
        noOldTrip = view.findViewById(R.id.captain_old_trip_empty);
        Log.e("text","HELLO");

        oldTrip = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        request = database.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        userId = firebaseUser.getUid();
        getOldTrip();
        //Toast.makeText(CaptainOldTripActivity.this, "Done", Toast.LENGTH_SHORT).show();
        return view;
    }

    private void getOldTrip() {
        progressBar.setVisibility(View.VISIBLE);
        //Log.e("text","HELLO");
        request.child("Captain Accepted Trips").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oldTrip.clear();
                //if (snapshot.child(userId).exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        OldTripModel test = data.getValue(OldTripModel.class);
                        //Log.e("text",test.getTripDate());
                        //assert test != null;
                        //if (test.getCaptainId().equals(userId)) {
                            oldTrip.add(test);

                        //}
                    }
                sendToAdapter(oldTrip);
               /* } else {
                    sendToAdapter(oldTrip);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendToAdapter(ArrayList<OldTripModel> oldTrip) {
        if (oldTrip.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            noOldTrip.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            noOldTrip.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            OldTripAdapter adapter = new OldTripAdapter(getContext(), oldTrip);
            recyclerView.setAdapter(adapter);
        }
    }


}

