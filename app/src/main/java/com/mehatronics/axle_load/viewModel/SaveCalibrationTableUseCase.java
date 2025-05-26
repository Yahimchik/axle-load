package com.mehatronics.axle_load.viewModel;

import com.mehatronics.axle_load.ble.repository.BluetoothRepository;

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
public class SaveCalibrationTableUseCase {

    private final BluetoothRepository bluetoothRepository;

    /**
     * Конструктор с внедрением зависимости BluetoothRepository.
     *
     * @param bluetoothRepository Репозиторий для взаимодействия с Bluetooth-устройством
     */
    @Inject
    public SaveCalibrationTableUseCase(BluetoothRepository bluetoothRepository) {
        this.bluetoothRepository = bluetoothRepository;
    }

    /**
     * Выполняет операцию сохранения таблицы калибровки.
     *
     * @return Код ошибки:
     *         <ul>
     *             <li>{@code 0} — успех</li>
     *             <li>{@code > 0} — код ошибки, например, недопустимый множитель</li>
     *         </ul>
     */
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
