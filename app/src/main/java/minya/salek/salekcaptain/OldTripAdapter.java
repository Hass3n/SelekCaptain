package minya.salek.salekcaptain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import minya.salek.salekcaptain.Model.OldTripModel;

public class OldTripAdapter extends RecyclerView.Adapter<OldTripAdapter.Holder> {

    Context context;
    ArrayList<OldTripModel> oldTrip;

    public OldTripAdapter(Context context, ArrayList<OldTripModel> oldTrip) {
        this.context = context;
        this.oldTrip = oldTrip;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_old_trip, parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        OldTripModel currentOldTrip = oldTrip.get(position);

        holder.tvDate.setText(currentOldTrip.getTripDate());
        holder.tvPlace.setText(currentOldTrip.getEndTrip());
        holder.tvPrice.setText(currentOldTrip.getTripPrice()+" ج");

    }

    @Override
    public int getItemCount() {
        return oldTrip.size();
    }


    class Holder extends RecyclerView.ViewHolder{
        TextView tvDate, tvPlace,tvPrice;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.old_trip_date_txt);
            tvPlace = itemView.findViewById(R.id.old_trip_place_txt);
            tvPrice = itemView.findViewById(R.id.old_trip_price_txt);
        }
    }
}
