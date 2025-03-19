package com.mehatronics.axle_load.utils;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.Locale;

public class DataUtils {
    public static String extractManufacturerData(ScanResult result) {
        ScanRecord scanRecord = result.getScanRecord();
        if (scanRecord != null) {
            SparseArray<byte[]> manufacturerData = scanRecord.getManufacturerSpecificData();
            if (manufacturerData != null && manufacturerData.size() > 0) {
                int manufacturerId = manufacturerData.keyAt(0);
                byte[] data = manufacturerData.get(manufacturerId);
                Log.d("MyTag", " " + Arrays.toString(data));
                if (data != null) {
                    if (data.length >= 3) {
                        int year = data[0];
                        int month = data[1];
                        int day = data[2];
                        return String.format(Locale.getDefault(), "%02d/%02d/%02d", day, month, year);
                    }
                }
            }
        }
        return "Неизвестно";
    }

    public static String extractData(byte[] data) {
        if (data != null) {
            if (data.length == 7) {
                int year = data[1] & 0xFF;
                year = year << 8;
                year += data[0] & 0xFF;
                int month = data[2];
                int day = data[3];
                return String.format(Locale.getDefault(), "%02d/%02d/%02d", day, month, year);
            } else if (data.length >= 3) {
                int year = data[0];
                int month = data[1];
                int day = data[2];
                return String.format(Locale.getDefault(), "%02d/%02d/%02d", day, month, year);
            } else {
                return String.valueOf(data[0]);
            }
        }
        return "Unknown";
    }

    public static String extractDetails(byte[] data, String type) {
        if ((data[0] & 0xff) == 0x07) {
            if ((data[1] & 0xff) == 0x02) {
                Log.d("MyTag", "Extract info");
                short iTempShort = 0;
                if (type.equals("pressure")) {
                    iTempShort |= (short) (data[5] & 0xff);
                    iTempShort <<= 8;
                    iTempShort |= (short) (data[4] & 0xff);
                    return String.valueOf((float) iTempShort / 10);
                }

                if (type.equals("weight")) {
                    iTempShort |= (short) (data[7] & 0xff);
                    iTempShort <<= 8;
                    iTempShort |= (short) (data[6] & 0xff);
                    return String.valueOf(iTempShort);
                }
            }
        }
        return null;
    }
}

