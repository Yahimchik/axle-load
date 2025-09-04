package com.mehatronics.axle_load.ui;

import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewInitializer {
    public static void initRecyclerView(View view, int id, RecyclerView.Adapter<?> adapter) {
        RecyclerView recyclerView = view.findViewById(id);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);
    }
}