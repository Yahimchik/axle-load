package com.mehatronics.axle_load.data.service;

import android.content.Context;
import android.net.Uri;

import com.mehatronics.axle_load.domain.entities.AxisModel;

import java.util.List;

public interface SaveToFileService {
    void saveAxisConfiguration(Context context, String category, String filePrefix, List<AxisModel> axisList);
    List<AxisModel> loadAxisConfigurationFromUri(Context context, Uri uri);
}
