package com.mehatronics.axle_load.domain.usecase.impl;

import com.mehatronics.axle_load.data.repository.BluetoothRepository;
import com.mehatronics.axle_load.domain.usecase.SaveCalibrationTableUseCase;

import javax.inject.Inject;

/**
 * UseCase (слой бизнес-логики) для сохранения таблицы калибровки в устройство.
 *
 * <p>Этот класс инкапсулирует шаги, необходимые для сохранения таблицы:
 * <ul>
 *     <li>Выполняет предварительную проверку/конвертацию множителя (multiplier)</li>
 *     <li>Получает текущее состояние устройства и таблицу калибровки</li>
 *     <li>Применяет таблицу к объекту {@code DeviceDetails}</li>
 *     <li>Сохраняет данные в сенсор</li>
 * </ul>
 * </p>
 *
 * <p>Возвращает код ошибки, если операция не может быть выполнена.</p>
 */
public class SaveCalibrationTableUseCaseImpl implements SaveCalibrationTableUseCase {

    private final BluetoothRepository bluetoothRepository;

    /**
     * Конструктор с внедрением зависимости BluetoothRepository.
     *
     * @param bluetoothRepository Репозиторий для взаимодействия с Bluetooth-устройством
     */
    @Inject
    public SaveCalibrationTableUseCaseImpl(BluetoothRepository bluetoothRepository) {
        this.bluetoothRepository = bluetoothRepository;
    }

    /**
     * Выполняет операцию сохранения таблицы калибровки.
     *
     * @return Код ошибки:
     * <ul>
     *     <li>{@code 0} — успех</li>
     *     <li>{@code > 0} — код ошибки, например, недопустимый множитель</li>
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