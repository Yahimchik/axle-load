package com.mehatronics.axle_load.ble.manager;

import static com.mehatronics.axle_load.utils.DataUtils.parsePressure;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.LOWER_MULTIPLIER_EDGE;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.MAX_MULTIPLIER;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.MIN_MULTIPLIER;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.UPPER_MULTIPLIER_EDGE;
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
        updateTable(originalPoints);
    }

    public void addPoint(CalibrationTable newPoint) {
        if (originalPoints == null) return;
        originalPoints.add(originalPoints.size() - 1, newPoint);
        updateTable(originalPoints);
    }

    public int convertMultiplier() {
        int validationError = validateTableValues();
        if (validationError > 0) return validationError;

        List<CalibrationTable> tableToSave = new ArrayList<>();

        CalibrationTable second = originalPoints.get(1);
        CalibrationTable penultimate = originalPoints.get(originalPoints.size() - 2);

        int lowerEdgeDetector = (int) (second.getDetector() * LOWER_MULTIPLIER_EDGE);
        int upperEdgeDetector = (int) (penultimate.getDetector() * UPPER_MULTIPLIER_EDGE);

        tableToSave.add(new CalibrationTable(lowerEdgeDetector, MIN_MULTIPLIER));
        tableToSave.add(new CalibrationTable(second.getDetector(), second.getMultiplier()));

        float weightFull = calculateAndAppendIntermediatePoints(tableToSave);

        tableToSave.add(new CalibrationTable(upperEdgeDetector, MAX_MULTIPLIER));
        tableToSave.set(0, new CalibrationTable(lowerEdgeDetector, weightFull));

        originalPoints.clear();
        originalPoints.addAll(tableToSave);

        updateTable(originalPoints);
        return 0;
    }

    private float calculateAndAppendIntermediatePoints(List<CalibrationTable> tableToSave) {
        float totalWeight = 0F;

        for (int i = 2; i < originalPoints.size() - 1; ++i) {
            CalibrationTable current = originalPoints.get(i);
            CalibrationTable previous = originalPoints.get(i - 1);

            int delta = current.getDetector() - previous.getDetector();
            float rate = current.getMultiplier() / delta;

            tableToSave.add(new CalibrationTable(current.getDetector(), rate));
            totalWeight += current.getMultiplier();
        }

        return totalWeight;
    }

    private int validateTableValues() {
        if (originalPoints == null || originalPoints.size() < 3) {
            Log.e("MyTag", "Invalid input: not enough points");
            return 1;
        }

        for (int i = 2; i < originalPoints.size() - 1; ++i) {
            CalibrationTable curr = originalPoints.get(i);
            CalibrationTable prev = originalPoints.get(i - 1);

            if (curr.getDetector() <= prev.getDetector()) {
                Log.e("MyTag", "Invalid detector at index " + i);
                return i;
            }

            if (curr.getMultiplier() <= 0) {
                Log.e("MyTag", "Invalid multiplier at index " + i);
                return i;
            }
        }
        return 0;
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