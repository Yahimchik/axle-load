package com.mehatronics.axle_load.ui.adapter;

import static com.mehatronics.axle_load.constants.FormatConstants.LANGUAGE_FLAGS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;

import java.util.List;
import java.util.function.Consumer;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LangViewHolder> {

    private final List<AppLanguage> languages;
    private final Consumer<AppLanguage> onLanguageSelected;

    public LanguageAdapter(List<AppLanguage> languages,
                           Consumer<AppLanguage> onLanguageSelected) {
        this.languages = languages;
        this.onLanguageSelected = onLanguageSelected;
    }

    @NonNull
    @Override
    public LangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lang_popup, parent, false);
        return new LangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LangViewHolder holder, int position) {
        AppLanguage lang = languages.get(position);
        holder.name.setText(lang.name());
        holder.setLanguage(lang);
        holder.itemView.setOnClickListener(v -> onLanguageSelected.accept(lang));
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public static class LangViewHolder extends RecyclerView.ViewHolder {
        ImageView flag;
        TextView name;

        LangViewHolder(@NonNull View itemView) {
            super(itemView);
            flag = itemView.findViewById(R.id.lang_flag);
            name = itemView.findViewById(R.id.lang_name);
        }

        private void setLanguage(AppLanguage selected) {
            Integer flag = LANGUAGE_FLAGS.get(selected);
            if (flag == null) {
                flag = R.drawable.ic_flag_united_kingdom;
            }
            this.flag.setImageResource(flag);
        }
    }
}