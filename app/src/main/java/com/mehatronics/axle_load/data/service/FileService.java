package com.mehatronics.axle_load.data.service;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import java.util.List;

public interface FileService {
    <T> void saveToFile(View rootView, Context context, String category, String filePrefix, List<T> list);

    <T> List<T> loadListFromUri(Context context, Uri uri, Class<T> clazz);
}