package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.format.InstallationPointFormatter.format;
import static com.mehatronics.axle_load.constants.StringConstants.ZERO;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CalibrationTableManager {

    @Inject
    public CalibrationTableManager() {
    }

    public void setUpInstallationPoint(SensorConfig sensorConfig, Spinner spinner) {
        List<String> pointOptions = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            pointOptions.add(format(i));
        }

        ArrayAdapter<String> pointAdapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, pointOptions);
        pointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(pointAdapter);

        int installationPoint = sensorConfig.getInstallationPoint();
        String description = format(installationPoint);
        int spinnerPosition = pointAdapter.getPosition(description);
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sensorConfig.setInstallationPoint(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void createCalibrationTable(CalibrationTableAdapter tableAdapter, DeviceDetails deviceDetails) {
        boolean isTableInitialized = tableAdapter.getItemCount() > 0;
        if (!isTableInitialized) {
            List<CalibrationTable> extendedTable = extendCalibrationTable(
                    deviceDetails.getTable(),
                    deviceDetails.getPressure()
            );
            tableAdapter.updateData(extendedTable);
        } else {
            CalibrationTable virtualPoint = getVirtualPoint(
                    deviceDetails.getTable(),
                    deviceDetails.getPressure()
            );
            tableAdapter.updateVirtualPoint(virtualPoint);
        }
    }

    public List<CalibrationTable> extendCalibrationTable(List<CalibrationTable> originalTable, String pressure) {
        List<CalibrationTable> extendedTable = new ArrayList<>(originalTable);

        if (originalTable.size() >= 2) {
            CalibrationTable virtualPoint = getCalibrationTable(originalTable, pressure);
            extendedTable.add(extendedTable.size() - 1, virtualPoint);
        }

        return extendedTable;
    }

    public CalibrationTable getVirtualPoint(List<CalibrationTable> originalTable, String pressure) {
        if (originalTable.size() < 2) return null;
        return getCalibrationTable(originalTable, pressure);
    }

    private CalibrationTable getCalibrationTable(List<CalibrationTable> originalTable, String pressure) {
        CalibrationTable last = originalTable.get(originalTable.size() - 2);
        float currentPressure = parsePressure(pressure);
        int detectorValue = (int) (currentPressure * 10);
        float multiplier = last.getMultiplier();
        return new CalibrationTable(detectorValue, multiplier);
    }

    private float parsePressure(String pressure) {
        if (ZERO.equals(pressure)) return 0f;
        try {
            return Float.parseFloat(pressure);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
}
