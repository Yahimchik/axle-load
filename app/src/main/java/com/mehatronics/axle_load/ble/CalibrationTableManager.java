package com.mehatronics.axle_load.ble;

import static com.mehatronics.axle_load.utils.DataUtils.parsePressure;
import static com.mehatronics.axle_load.utils.diffUtil.CalibrationDiffUtil.hasTableChanged;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CalibrationTableManager {
    private final MutableLiveData<List<CalibrationTable>> tableLiveData = new MutableLiveData<>();
    private List<CalibrationTable> originalPoints = new ArrayList<>();
    private List<CalibrationTable> initialPoints = new ArrayList<>();

    @Inject
    public CalibrationTableManager() {
    }

    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return tableLiveData;
    }

    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        originalPoints = deviceDetails.getTable();

        if (initialPoints.isEmpty()) {
            initialPoints = new ArrayList<>(originalPoints);
        }

        aupdateVirtualPoint(deviceDetails);
    }

    public void deletePoint(CalibrationTable item) {
        if (originalPoints == null) return;
        originalPoints.remove(item);
        updateTable(new ArrayList<>(originalPoints));
    }

    public void addPoint(CalibrationTable newPoint) {
        if (originalPoints == null) return;
        originalPoints.add(originalPoints.size() - 1, newPoint);
        Log.d("MyTag", originalPoints.toString());
        updateTable(new ArrayList<>(originalPoints));
    }

    private void aupdateVirtualPoint(DeviceDetails details) {
        List<CalibrationTable> displayed = new ArrayList<>(originalPoints);
        try {
            if (!displayed.isEmpty()) {
                int pressure = parsePressure(details.getPressure());
                var virtual = new CalibrationTable(pressure, 10, true);
                displayed.add(displayed.size() - 1, virtual);
            }
            updateTable(displayed);
        } catch (NumberFormatException e) {
            Log.w("MyTag", "Invalid pressure format: " + details.getPressure());
        }
    }

    private void updateTable(List<CalibrationTable> table) {
        List<CalibrationTable> current = tableLiveData.getValue();
        if (hasTableChanged(current, table)) {
            tableLiveData.setValue(new ArrayList<>(table));
        }
    }
}