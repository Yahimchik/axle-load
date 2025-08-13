package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.R.string.save_to_file_not_allowed_at;
import static com.mehatronics.axle_load.R.string.saved_in;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mehatronics.axle_load.data.service.SaveToFileService;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.notification.SnackbarManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SaveToFileServiceImpl implements SaveToFileService {
    private final Gson gson = new Gson();
    private final ResourceProvider provider;
    private final SnackbarManager manager;

    @Inject
    public SaveToFileServiceImpl(ResourceProvider provider, SnackbarManager manager) {
        this.provider = provider;
        this.manager = manager;
    }

    @Override
    public <T> void saveToFile(View rootView, Context context, String category, String filePrefix, List<T> list) {
        String fileName = filePrefix + "_" +
                new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
                        .format(new Date()) + ".txt";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + category);
        }

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        } else {
            manager.showMessage(rootView, provider.getString(save_to_file_not_allowed_at));
            return;
        }

        if (uri == null) {
            manager.showMessage(rootView, "Не удалось создать файл");
            return;
        }

        String json = gson.toJson(list);

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                outputStream.write(json.getBytes());
                manager.showMessage(rootView, provider.getString(saved_in, category + "/" + fileName));
            } else {
                manager.showMessage(rootView, "Не удалось открыть поток");
            }
        } catch (IOException e) {
            manager.showMessage(rootView, "Ошибка при сохранении файла");
        }
    }

    @Override
    public <T> List<T> loadListFromUri(Context context, Uri uri, Class<T> clazz) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getContentResolver().openInputStream(uri)))) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String json = builder.toString();

            JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
            if (jsonArray.isEmpty()) {
                return new ArrayList<>();
            }

            Set<String> requiredFields = Arrays.stream(clazz.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());

            JsonObject firstObj = jsonArray.get(0).getAsJsonObject();
            for (String field : requiredFields) {
                if (!firstObj.has(field)) {
                    return new ArrayList<>();
                }
            }

            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(json, listType);

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}