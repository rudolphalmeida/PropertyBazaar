package com.example.rudolph.propertybazaar.adapters;

import android.content.Context;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rudolph.propertybazaar.R;
import com.example.rudolph.propertybazaar.models.Property;
import com.example.rudolph.propertybazaar.models.User;
import com.example.rudolph.propertybazaar.rest.APIClient;
import com.example.rudolph.propertybazaar.rest.APIInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rudolph Almeida on 3/12/2017.
 */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> properties;
    private int rowLayout;
    private Context context;
    private APIInterface apiservice;
    private int mExpandedPosition = -1;

    public PropertyAdapter(List<Property> properties, int rowLayout, Context context) {
        this.properties = properties;
        this.rowLayout = rowLayout;
        this.context = context;

        apiservice = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public PropertyAdapter.PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PropertyViewHolder holder, final int position) {

        final boolean isExpanded = position == mExpandedPosition;
        holder.detailView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                TransitionManager.beginDelayedTransition(holder.detailView);
                notifyDataSetChanged();
            }
        });

        holder.title.setText(properties.get(position).getTitle());
        holder.address.setText(properties.get(position).getAddress());
        holder.garages.setText(Integer.toString(properties.get(position).getGarages()));
        holder.bedrooms.setText(Integer.toString(properties.get(position).getBedrooms()));
        holder.bathrooms.setText(Integer.toString(properties.get(position).getBathrooms()));
        holder.price.setText(Integer.toString(properties.get(position).getPrice()));
        holder.rooms.setText(Integer.toString(properties.get(position).getRooms()));
        holder.description.setText(properties.get(position).getDescription());
        holder.area.setText(Integer.toString(properties.get(position).getArea()) + " sq. feet.");

        final String userUrl = properties.get(position).getOwner();
        int id = Integer.parseInt(userUrl.substring(46, userUrl.lastIndexOf('/')));
        Log.d("Parsed User Id", Integer.toString(id));

        Call<User> call = apiservice.getUser(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                //User user = response.body();
                Log.d("Property Adapter", response.body().toString());
                holder.owner_name.setText("Owner: " + response.body().getFirst_name() + " " + response.body().getLast_name());
                holder.owner_email.setText("Email: " + response.body().getEmail());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Property Adapter", t.toString());
            }
        });

        Picasso.with(context).load(properties.get(position).getImage()).into(holder.cover);

    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView title, address, description, owner_name, owner_email;
        TextView bedrooms, bathrooms, garages, rooms, price, area;
        LinearLayout detailView;

        public PropertyViewHolder(View v) {
            super(v);
            cover = (ImageView) v.findViewById(R.id.iv_property_cover);
            title = (TextView) v.findViewById(R.id.tv_property_title);
            address = (TextView) v.findViewById(R.id.tv_property_address);
            bedrooms = (TextView) v.findViewById(R.id.tv_property_bedrooms);
            bathrooms = (TextView) v.findViewById(R.id.tv_property_bathrooms);
            garages = (TextView) v.findViewById(R.id.tv_property_garages);
            rooms = (TextView) v.findViewById(R.id.tv_property_rooms);
            price = (TextView) v.findViewById(R.id.tv_property_price);
            description = (TextView) v.findViewById(R.id.tv_property_description);
            area = (TextView) v.findViewById(R.id.tv_property_area);
            owner_name = (TextView) v.findViewById(R.id.tv_owner_name);
            owner_email = (TextView) v.findViewById(R.id.tv_owner_email);
            detailView = (LinearLayout) v.findViewById(R.id.ll_detail_property_view);
        }

    }

}
