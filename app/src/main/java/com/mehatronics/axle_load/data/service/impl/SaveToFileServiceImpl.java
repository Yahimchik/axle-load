package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.R.string.*;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.service.SaveToFileService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.localization.ResourceProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class SaveToFileServiceImpl implements SaveToFileService {
    private final Gson gson = new Gson();
    private final ResourceProvider provider;

    @Inject
    public SaveToFileServiceImpl(ResourceProvider provider) {
        this.provider = provider;
    }

    @Override
    public void saveAxisConfiguration(Context context, String category, String filePrefix, List<AxisModel> axisList) {
        String fileName = filePrefix + "_" +
                new SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale.getDefault()).format(new Date()) + ".txt";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "text/plain"); // <--- ключевое изменение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + category);
        }

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        } else {
            Toast.makeText(context, provider.getString(save_to_file_not_allowed_at), Toast.LENGTH_SHORT).show();
            return;
        }

        if (uri == null) {
            Toast.makeText(context, "Не удалось создать файл", Toast.LENGTH_SHORT).show();
            return;
        }

        String json = gson.toJson(axisList); // <--- JSON-содержимое

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                outputStream.write(json.getBytes());
                Toast.makeText(context, provider.getString(saved_in) + category + "/" + fileName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Не удалось открыть поток", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Ошибка при сохранении файла", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public List<AxisModel> loadAxisConfigurationFromUri(Context context, Uri uri) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getContentResolver().openInputStream(uri)))) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String json = builder.toString();

            return gson.fromJson(json, new TypeToken<List<AxisModel>>() {
            }.getType());

        } catch (IOException e) {
            Toast.makeText(context, provider.getString(error_reading_the_file), Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }
}
