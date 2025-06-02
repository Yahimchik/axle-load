package com.mehatronics.axle_load.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.ui.adapter.AxisAdapter;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.viewModel.ConfigureViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends Fragment implements MessageCallback {
    @Inject
    protected FragmentNavigator fragmentNavigator;
    private EditText editTextAxisCount;
    private Button buttonConfigure;
    private ConfigureViewModel viewModel;
    private View root;
    private RecyclerView recyclerView;
    private AxisAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_configure, container, false);

        editTextAxisCount = root.findViewById(R.id.editTextAxisCount);
        buttonConfigure = root.findViewById(R.id.buttonConfigure);

        recyclerView = root.findViewById(R.id.recyclerViewAxes);
        viewModel = new ViewModelProvider(this).get(ConfigureViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupListeners();

        return root;
    }

    private void setupRecyclerView() {
        adapter = new AxisAdapter((num, side) -> viewModel.onWheelClicked(num, side));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getAxisList().observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel.getMessage().observe(getViewLifecycleOwner(), this::showMessage);
        viewModel.getAxisClick().observe(getViewLifecycleOwner(),this::handleAxisClickEvent);
    }

    private void handleAxisClickEvent(Event<InstalationPoint> event) {
        InstalationPoint data = event.getContentIfNotHandled();
        if (data != null) {
            fragmentNavigator.showFragment(new AvailableSensorFragment());
        }
    }

    private void setupListeners() {
        buttonConfigure.setOnClickListener(v -> {
            String input = editTextAxisCount.getText().toString().trim();
            viewModel.onConfigureClicked(input);
        });
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }
}
