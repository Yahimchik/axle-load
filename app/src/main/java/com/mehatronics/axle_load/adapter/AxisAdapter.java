package com.mehatronics.axle_load.adapter;

import static com.mehatronics.axle_load.constants.StringConstants.AXLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.fragment.SensorSettingsFragment;
import com.mehatronics.axle_load.viewModel.SensorViewModel;

public class AxisAdapter extends RecyclerView.Adapter<AxisAdapter.ViewHolder> {
    private int numberOfAxes;
    private final SensorViewModel sensorViewModel;
    private final LifecycleOwner lifecycleOwner;

    public AxisAdapter(int numberOfAxes, SensorViewModel sensorViewModel, LifecycleOwner lifecycleOwner) {
        this.numberOfAxes = numberOfAxes;
        this.sensorViewModel = sensorViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNumberOfAxes(int numberOfAxes) {
        this.numberOfAxes = numberOfAxes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_axis, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.axisTitle.setText(AXLE + (position + 1));
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberOfAxes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView axisTitle;
        private final ImageView axleLeft;
        private final ImageView axisRight;
        private Observer<String> leftObserver;
        private Observer<String> rightObserver;

        public ViewHolder(View itemView) {
            super(itemView);
            axisTitle = itemView.findViewById(R.id.axisTitle);
            axleLeft = itemView.findViewById(R.id.axleSensorLeft);
            axisRight = itemView.findViewById(R.id.axleSensorRight);
        }

        void bind(int axisIndex) {
            clearObservers();

            leftObserver = image -> axleLeft.setImageResource(getDrawableForSensor(image, "left"));
            rightObserver = image -> axisRight.setImageResource(getDrawableForSensor(image, "right"));

            sensorViewModel.getSensorImage(axisIndex, "left").observe(lifecycleOwner, leftObserver);
            sensorViewModel.getSensorImage(axisIndex, "right").observe(lifecycleOwner, rightObserver);

            axleLeft.setOnClickListener(v -> openSensorSettingsFragment(v, "left", axisIndex));
            axisRight.setOnClickListener(v -> openSensorSettingsFragment(v, "right", axisIndex));
        }

        void clearObservers() {
            if (leftObserver != null)
                sensorViewModel.getSensorImage(getAdapterPosition(), "left").removeObserver(leftObserver);
            if (rightObserver != null)
                sensorViewModel.getSensorImage(getAdapterPosition(), "right").removeObserver(rightObserver);
        }

        private int getDrawableForSensor(String image, String position) {
            switch (position) {
                case "left":
                    return "axle_sensor_left".equals(image) ? R.drawable.axle_sensor_left : R.drawable.axle_left;
                case "right":
                    return "axle_sensor_right".equals(image) ? R.drawable.axle_sensor_right : R.drawable.axle_right;
                default:
                    return 0;
            }
        }
    }

    private void openSensorSettingsFragment(View v, String sensorPosition, int axisIndex) {
        FragmentTransaction transaction = ((AppCompatActivity) v.getContext())
                .getSupportFragmentManager().beginTransaction();
        SensorSettingsFragment fragment = new SensorSettingsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("sensorPosition", sensorPosition);
        bundle.putInt("axisIndex", axisIndex);
        fragment.setArguments(bundle);

        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
