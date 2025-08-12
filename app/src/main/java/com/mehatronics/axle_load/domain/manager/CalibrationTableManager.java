package com.mehatronics.axle_load.domain.manager;

import static com.mehatronics.axle_load.constants.ValueConstants.LOWER_MULTIPLIER_EDGE;
import static com.mehatronics.axle_load.constants.ValueConstants.MAX_MULTIPLIER;
import static com.mehatronics.axle_load.constants.ValueConstants.MIN_MULTIPLIER;
import static com.mehatronics.axle_load.constants.ValueConstants.UPPER_MULTIPLIER_EDGE;
import static com.mehatronics.axle_load.utils.DataUtils.parsePressure;
import static com.mehatronics.axle_load.ui.adapter.diffUtil.CalibrationDiffUtil.hasTableChanged;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Менеджер для работы с таблицей калибровки.
 *
 * <p>Отвечает за управление точками калибровки, валидацию, пересчёт коэффициентов (multiplier),
 * добавление виртуальной точки, а также за предоставление LiveData-объекта для UI.</p>
 */
public class CalibrationTableManager {
    private final MutableLiveData<List<CalibrationTable>> tableLiveData = new MutableLiveData<>();
    private List<CalibrationTable> originalPoints = new ArrayList<>();
    private List<CalibrationTable> initialPoints = new ArrayList<>();

    /**
     * Конструктор с внедрением зависимостей.
     */
    @Inject
    public CalibrationTableManager() {
    }

    /**
     * Возвращает LiveData для отслеживания состояния таблицы калибровки.
     *
     * @return LiveData списка точек калибровки.
     */
    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return tableLiveData;
    }

    public void setCalibrationTable(List<CalibrationTable> table){
        if (originalPoints.size() >= 2) {
            CalibrationTable first = originalPoints.get(0);
            CalibrationTable last = originalPoints.get(originalPoints.size() - 1);

            originalPoints.clear();
            originalPoints.add(first);
            originalPoints.addAll(table); // вставляем новые
            originalPoints.add(last);
        } else {
            originalPoints.clear();
            originalPoints.addAll(table);
        }

        if (initialPoints.size() >= 2) {
            CalibrationTable first = initialPoints.get(0);
            CalibrationTable last = initialPoints.get(initialPoints.size() - 1);

            initialPoints.clear();
            initialPoints.add(first);
            initialPoints.addAll(table);
            initialPoints.add(last);
        } else {
            initialPoints.clear();
            initialPoints.addAll(table);
        }

        tableLiveData.setValue(new ArrayList<>(originalPoints));
    }

    /**
     * Обновляет виртуальную точку на основе давления, полученного от устройства.
     *
     * @param deviceDetails Детали устройства, включая давление.
     */
    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        originalPoints = deviceDetails.getTable();

        if (initialPoints.isEmpty()) {
            initialPoints = new ArrayList<>(originalPoints);
        }

        aupdateVirtualPoint(deviceDetails);
    }

    /**
     * Удаляет точку калибровки из таблицы.
     *
     * @param item Точка калибровки для удаления.
     */
    public void deletePoint(CalibrationTable item) {
        if (originalPoints == null) return;
        originalPoints.remove(item);
        updateTable(originalPoints);
    }

    /**
     * Добавляет новую точку калибровки перед виртуальной.
     *
     * @param newPoint Новая точка калибровки.
     */
    public void addPoint(CalibrationTable newPoint) {
        if (originalPoints == null) return;
        originalPoints.add(originalPoints.size() - 1, newPoint);
        updateTable(originalPoints);
    }

    /**
     * Валидирует и пересчитывает множители (multiplier), подготавливая таблицу для сохранения.
     *
     * @return Код ошибки:
     *         <ul>
     *             <li>0 — успех</li>
     *             <li>>0 — индекс строки с ошибкой</li>
     *         </ul>
     */
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

    /**
     * Вычисляет промежуточные множители и добавляет точки в таблицу.
     *
     * @param tableToSave Таблица, в которую будут добавлены промежуточные точки.
     * @return Общий вес (сумма множителей).
     */
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

    /**
     * Проверяет корректность данных в таблице:
     * <ul>
     *     <li>Количество точек ≥ 3</li>
     *     <li>Увеличение значений детектора</li>
     *     <li>Множители > 0</li>
     * </ul>
     *
     * @return 0 если всё корректно, иначе индекс строки с ошибкой.
     */
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

    /**
     * Внутренний метод для добавления виртуальной точки в таблицу.
     *
     * @param details Детали устройства, включая текущее давление.
     */
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

    /**
     * Обновляет LiveData таблицы, если были изменения.
     *
     * @param table Обновлённый список точек.
     */
    private void updateTable(List<CalibrationTable> table) {
        List<CalibrationTable> current = tableLiveData.getValue();
        if (hasTableChanged(current, table)) {
            tableLiveData.setValue(new ArrayList<>(table));
        }
    }
}
