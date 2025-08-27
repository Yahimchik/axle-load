package com.mehatronics.axle_load.domain.usecase.impl;

import com.mehatronics.axle_load.data.repository.impl.BluetoothRepository;
import com.mehatronics.axle_load.domain.usecase.SaveCalibrationTableUseCase;

import javax.inject.Inject;

/**
 * Реализация UseCase (слоя бизнес-логики) для сохранения таблицы калибровки в устройство.
 *
 * <p>Этот класс инкапсулирует следующие шаги:
 * <ul>
 *     <li>Проверка и конвертация множителя</li>
 *     <li>Получение текущих данных устройства и таблицы</li>
 *     <li>Применение таблицы к {@code DeviceDetails}</li>
 *     <li>Запуск сохранения таблицы на сенсоре</li>
 * </ul>
 * </p>
 *
 * <p>Метод {@link #execute()} возвращает код выполнения операции, где 0 означает успех.</p>
 */
public class SaveCalibrationTableUseCaseImpl implements SaveCalibrationTableUseCase {

    private final BluetoothRepository bluetoothRepository;

    /**
     * Конструктор, внедряющий {@link BluetoothRepository}.
     *
     * @param bluetoothRepository Репозиторий для взаимодействия с BLE-устройством
     */
    @Inject
    public SaveCalibrationTableUseCaseImpl(BluetoothRepository bluetoothRepository) {
        this.bluetoothRepository = bluetoothRepository;
    }

    /**
     * Запускает процесс сохранения таблицы калибровки.
     * <p>
     * Шаги:
     * <ol>
     *     <li>Конвертирует множитель через {@code convertMultiplier()}</li>
     *     <li>Получает текущие {@code DeviceDetails} и таблицу</li>
     *     <li>Устанавливает таблицу в {@code DeviceDetails}</li>
     *     <li>Сохраняет таблицу в сенсор через {@code saveTableToSensor()}</li>
     * </ol>
     *
     * @return код ошибки:
     * <ul>
     *     <li>{@code 0} — успех</li>
     *     <li>{@code > 0} — ошибка (например, некорректный множитель)</li>
     * </ul>
     */
    @Override
    public int execute() {
        int error = bluetoothRepository.convertMultiplier();
        if (error > 0) {
            return error;
        }

        var details = bluetoothRepository.getDeviceDetailsLiveData().getValue();
        var table = bluetoothRepository.getCalibrationTable().getValue();

        if (details == null || table == null) {
            return error;
        }

        details.setTable(table);
        bluetoothRepository.setDeviceDetailsLiveData(details);

        bluetoothRepository.saveTableToSensor();

        return error;
    }
}