package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.utils.constants.StringConstants.AXLE;
import static com.mehatronics.axle_load.utils.constants.StringConstants.LEFT;
import static com.mehatronics.axle_load.utils.constants.StringConstants.RIGHT;
import static com.mehatronics.axle_load.utils.constants.StringConstants.ZERO;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.databinding.ActivityDdsBinding;
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
            CalibrationTable virtualPoint = getCalibrationTable(originalTable,pressure);
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

    public String getInstallationPointDescription(int installationPoint) {
        int axle = (installationPoint - 1) / 2 + 1;
        String position = (installationPoint - 1) % 2 == 0 ? LEFT : RIGHT;
        return AXLE + " " + axle + " — " + position;
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
