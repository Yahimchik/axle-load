package com.mehatronics.axle_load.adapter;

import static com.mehatronics.axle_load.entities.enums.AxisSide.CENTER;
import static com.mehatronics.axle_load.entities.enums.AxisSide.LEFT;
import static com.mehatronics.axle_load.entities.enums.AxisSide.RIGHT;
import static com.mehatronics.axle_load.utils.constants.StringConstants.AXLE;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.listener.OnAxisClickListener;
import com.mehatronics.axle_load.entities.AxisModel;

import java.util.ArrayList;
import java.util.List;

public class AxisAdapter extends RecyclerView.Adapter<AxisAdapter.AxisViewHolder> {
    private List<AxisModel> axisList = new ArrayList<>();
    private final OnAxisClickListener listener;

    public AxisAdapter(OnAxisClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(List<AxisModel> list) {
        axisList = list;
        Log.d("MyTag", axisList.toString());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AxisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_axis, parent, false);
        return new AxisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AxisViewHolder holder, int position) {
        AxisModel axis = axisList.get(position);
        holder.bind(axis);
    }

    @Override
    public int getItemCount() {
        return axisList.size();
    }

    public class AxisViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        Button resetButton;
        ImageView axleSensorLeft;
        ImageView axleSensorCenter;
        ImageView axleSensorRight;

        AxisViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.axisTitle);
            resetButton = itemView.findViewById(R.id.buttonReset);
            axleSensorLeft = itemView.findViewById(R.id.axleSensorLeft);
            axleSensorCenter = itemView.findViewById(R.id.axleSensorCenter);
            axleSensorRight = itemView.findViewById(R.id.axleSensorRight);
        }

        @SuppressLint("SetTextI18n")
        void bind(AxisModel axis) {
            title.setText(AXLE + " " + axis.number());

            axleSensorLeft.setOnClickListener(v -> listener.onClick(axis.number(), LEFT));
            axleSensorCenter.setOnClickListener(v -> listener.onClick(axis.number(), CENTER));
            axleSensorRight.setOnClickListener(v -> listener.onClick(axis.number(), RIGHT));
        }
    }
}
