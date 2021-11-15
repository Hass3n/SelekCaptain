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

import minya.salek.salekcaptain.Model.UserModel;
import minya.salek.salekcaptain.NewTripAdapter;
import minya.salek.salekcaptain.R;
import minya.salek.salekcaptain.TripModel;


public class TripRequestFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<UserModel> userTrip;
    private ArrayList<TripModel> tripModels;
    private DatabaseReference request;
    private String userId;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView noOldTrip;

    public TripRequestFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_trip, container, false);
        recyclerView = view.findViewById(R.id.new_trip_recycler);
        progressBar = view.findViewById(R.id.captain_new_trip_load);
        noOldTrip = view.findViewById(R.id.captain_new_trip_empty);


        userTrip = new ArrayList<>();
        tripModels = new ArrayList<>();

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
        request.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripModels.clear();
                if (snapshot.child(userId).exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        TripModel tripModel = data.getValue(TripModel.class);
                        tripModels.add(tripModel);
                        if (tripModel.getCaptainId().equals(userId)) {
                            progressBar.setVisibility(View.GONE);
                            sendToAdapter(tripModels);
                        }
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    sendToAdapter(tripModels);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendToAdapter(ArrayList<TripModel> userTrip) {
        if (userTrip.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            noOldTrip.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            noOldTrip.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            NewTripAdapter adapter = new NewTripAdapter(getContext(), userTrip);
            recyclerView.setAdapter(adapter);
        }
    }


}

