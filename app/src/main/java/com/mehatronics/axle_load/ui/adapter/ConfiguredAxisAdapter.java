package com.mehatronics.axle_load.ui.adapter;

import static android.view.LayoutInflater.from;
import static com.mehatronics.axle_load.R.drawable.axle_center;
import static com.mehatronics.axle_load.R.drawable.axle_center_configured;
import static com.mehatronics.axle_load.R.drawable.axle_left;
import static com.mehatronics.axle_load.R.drawable.axle_left_configured;
import static com.mehatronics.axle_load.R.drawable.axle_right;
import static com.mehatronics.axle_load.R.drawable.axle_right_configured;
import static com.mehatronics.axle_load.R.layout.axis_configured;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.ui.adapter.diffUtil.AxisUiDiffUtil;

public class ConfiguredAxisAdapter extends ListAdapter<AxisUiModel, ConfiguredAxisAdapter.AxisViewHolder> {
    public ConfiguredAxisAdapter() {
        super(new AxisUiDiffUtil());
    }

    @NonNull
    @Override
    public AxisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = from(parent.getContext()).inflate(axis_configured, parent, false);
        return new AxisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AxisViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class AxisViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView macLeft;
        TextView macRight;
        TextView weightLeft;

        TextView weightRight;
        TextView pressureLeft;
        TextView pressureRight;

        ImageView leftIcon;
        ImageView rightIcon;
        ImageView centerIcon;

        LinearLayout leftInfoContainer;
        LinearLayout rightInfoContainer;

        public AxisViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            macLeft = itemView.findViewById(R.id.leftInfoMacValue);
            weightLeft = itemView.findViewById(R.id.leftInfoWeightValue);
            pressureLeft = itemView.findViewById(R.id.leftInfoPressureValue);

            macRight = itemView.findViewById(R.id.rightInfoMacValue);
            weightRight = itemView.findViewById(R.id.rightInfoWeightValue);
            pressureRight = itemView.findViewById(R.id.rightInfoPressureValue);

            leftIcon = itemView.findViewById(R.id.axleSensorLeft);
            rightIcon = itemView.findViewById(R.id.axleSensorRight);
            centerIcon = itemView.findViewById(R.id.axleSensorCenter);

            leftInfoContainer = itemView.findViewById(R.id.leftInfoContainer);
            rightInfoContainer = itemView.findViewById(R.id.rightInfoContainer);
        }

        public void bind(AxisUiModel model) {
            title.setText(model.title());

            macLeft.setText(safeText(model.macLeft()));
            weightLeft.setText(safeText(model.weightLeft()));
            pressureLeft.setText(safeText(model.pressureLeft()));

            if (model.macCenter() != null) {
                macRight.setText(safeText(model.macCenter()));
                rightIcon.setImageResource(axle_right);
                centerIcon.setImageResource(axle_center_configured);
            } else {
                macRight.setText(safeText(model.macRight()));
                rightIcon.setImageResource(model.macRight() != null ? axle_right_configured : axle_right);
                centerIcon.setImageResource(axle_center);
            }

            weightRight.setText(safeText(model.weightRight()));
            pressureRight.setText(safeText(model.pressureRight()));

            leftIcon.setImageResource(model.macLeft() != null ? axle_left_configured : axle_left);

            leftInfoContainer.setVisibility(model.macLeft() != null ? View.VISIBLE : View.INVISIBLE);
            rightInfoContainer.setVisibility((model.macRight() != null || model.macCenter() != null) ? View.VISIBLE : View.INVISIBLE);
        }

        private String safeText(String text) {
            return text != null ? text : "--";
        }
    }
}