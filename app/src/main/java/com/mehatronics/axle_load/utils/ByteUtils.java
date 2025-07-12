package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.constants.ValueConstants.MAX_DETECTORS;
import static com.mehatronics.axle_load.constants.ValueConstants.MAX_MULTIPLIER;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.domain.entities.CalibrationParseResult;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.SensorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для работы с байтами, преобразованиями чисел и строк,
 * а также для сериализации и десериализации данных калибровки и конфигурации сенсоров.
 * Применяется при взаимодействии с устройствами axle load.
 */
public class ByteUtils {

    /**
     * Преобразует список калибровочных точек из мультипликаторов в порционные значения.
     *
     * @param table Список объектов {@link CalibrationTable}, который будет преобразован.
     */
    public static void convertMultiplierToPortion(List<CalibrationTable> table) {
        List<CalibrationTable> result = new ArrayList<>();
        result.add(table.get(0));
        for (int i = 0; i < table.size() - 1; ++i) {
            if (i > 0) {
                CalibrationTable curr = table.get(i);
                CalibrationTable prev = table.get(i - 1);

                int detecor = curr.getDetector();
                float multiplier = calculateMultiplier(curr, prev);

                result.add(new CalibrationTable(detecor, multiplier));
            }
        }

        result.add(table.get(table.size() - 1));

        table.clear();
        table.addAll(result);
    }

    /**
     * Записывает 4 байта float-множителя в буфер по определённой позиции.
     *
     * @param buffer  Целевой массив байт.
     * @param i       Индекс множителя.
     * @param intBits Представление float в виде int (через Float.floatToIntBits).
     */
    public static void multiplierToBytes(byte[] buffer, int i, int intBits) {
        buffer[i * 6 + 6] = (byte) (intBits & 0xff);
        buffer[i * 6 + 7] = (byte) ((intBits >> 8) & 0xff);
        buffer[i * 6 + 8] = (byte) ((intBits >> 16) & 0xff);
        buffer[i * 6 + 9] = (byte) ((intBits >> 24) & 0xff);
    }

    /**
     * Записывает 2 байта детектора в буфер по определённой позиции.
     *
     * @param buffer  Целевой массив байт.
     * @param i       Индекс детектора.
     * @param intBits Целочисленное значение детектора.
     */
    public static void detectorToBytes(byte[] buffer, int i, int intBits) {
        buffer[i * 6 + 4] = (byte) (intBits & 0xff);
        buffer[i * 6 + 5] = (byte) ((intBits >> 8) & 0xff);
    }

    /**
     * Записывает 4 байта из int в массив байт с заданного индекса.
     *
     * @param value   Массив байт.
     * @param intBits Целое значение.
     * @param index   Индекс начала записи.
     */
    public static void intToFourBytes(byte[] value, int intBits, int index) {
        value[index] = (byte) (intBits & ZERO_COMMAND_BINARY);
        value[index + 1] = (byte) ((intBits >> 8) & ZERO_COMMAND_BINARY);
        value[index + 2] = (byte) ((intBits >> 16) & ZERO_COMMAND_BINARY);
        value[index + 3] = (byte) ((intBits >> 24) & ZERO_COMMAND_BINARY);
    }

    /**
     * Записывает 2 байта из int в массив байт с заданного индекса.
     *
     * @param value   Массив байт.
     * @param intBits Целое значение.
     * @param index   Индекс начала записи.
     */
    public static void intToTwoBytes(byte[] value, int intBits, int index) {
        value[index] = (byte) (intBits & ZERO_COMMAND_BINARY);
        value[index + 1] = (byte) ((intBits >> 8) & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует два байта в целое значение.
     *
     * @param bytes  Массив байт.
     * @param first  Индекс старшего байта.
     * @param second Индекс младшего байта.
     * @return Целое значение.
     */
    public static int convertByteToValue(byte[] bytes, int first, int second) {
        return parseIntFromBytes(bytes, first, second);
    }

    /**
     * Альтернативный метод преобразования двух байт в целое значение.
     *
     * @param bytes  Массив байт.
     * @param first  Индекс старшего байта.
     * @param second Индекс младшего байта.
     * @return Целое значение.
     */
    public static int convertBytesToValue(byte[] bytes, int first, int second) {
        return (bytes[first] & ZERO_COMMAND_BINARY) * 256 + (bytes[second] & ZERO_COMMAND_BINARY);
    }


    /**
     * Записывает один байт из int в массив байт по указанному индексу.
     *
     * @param value   Массив байт.
     * @param intBits Значение.
     * @param index   Индекс.
     */
    public static void intToBytes(byte[] value, int intBits, int index) {
        value[index] = (byte) (intBits & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует строку в байты и записывает в массив по позициям 30–39.
     *
     * @param value       Целевой массив.
     * @param stateNumber Исходная строка (например, гос. номер).
     */
    public static void stringToBytes(byte[] value, String stateNumber) {
        for (int i = 30; i < 40; ++i) {
            value[i] = 0x20;
        }

        char[] mass = stateNumber.toCharArray();

        for (int i = 0; i < mass.length; i++) {
            value[30 + i] = (byte) mass[i];
        }
    }

    /**
     * Преобразует байты из устройства в список калибровочных точек.
     *
     * @param bytes Массив байт.
     * @param table Список таблицы, куда будут добавлены точки.
     * @param page  Текущая страница.
     * @return {@link CalibrationParseResult} с результатом и следующей страницей.
     */
    public static CalibrationParseResult convertBytesToCalibrationTable(byte[] bytes, List<CalibrationTable> table, int page) {
        if (!isCalibrationCommand(bytes)) {
            return new CalibrationParseResult(page, false);
        }

        for (int i = 0; i < MAX_DETECTORS; i++) {
            int detector = convertToDetector(bytes, i);
            if (detector == 0) break;

            int multiplier = convertToMultiplier(bytes, i);
            float value = Float.intBitsToFloat(multiplier);

            table.add(new CalibrationTable(detector, value));
            if (value == MAX_MULTIPLIER) return new CalibrationParseResult(0, true);
        }

        int nextPage = (page < 1) ? page + 1 : -1;
        boolean tableCompleted = (nextPage == 0 || nextPage == -1);
        return new CalibrationParseResult(nextPage, tableCompleted);
    }

    /**
     * Проверяет, является ли команда командой калибровки.
     *
     * @param bytes Входной массив байт.
     * @return true, если это команда калибровки.
     */
    public static boolean isCalibrationCommand(byte[] bytes) {
        return (bytes[0] & ZERO_COMMAND_BINARY) == FIRST_COMMAND;
    }

    /**
     * Извлекает строку из массива байт.
     *
     * @param data       Массив байт.
     * @param startIndex Начальный индекс.
     * @param length     Длина строки.
     * @return Полученная строка.
     */
    public static String extractStringFromBytes(byte[] data, int startIndex, int length) {
        if (data == null || data.length < startIndex + length) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append((char) (data[startIndex + i] & 0xFF));
        }

        return result.toString();
    }

    /**
     * Преобразует массив байт в объект конфигурации сенсора.
     *
     * @param bytes Массив байт.
     * @return Объект {@link SensorConfig}.
     */
    public static SensorConfig convertBytesToConfiguration(BluetoothGatt gatt, byte[] bytes) {
        return new SensorConfig.Builder()
                .setMac(gatt.getDevice().getAddress())
                .setFlagSystem(parseIntFromBytes(bytes, 7))
                .setConfigSystem(parseIntFromBytes(bytes, 11))
                .setMultiplier(Float.intBitsToFloat(parseIntFromBytes(bytes, 15)))
                .setOffset(Float.intBitsToFloat(parseIntFromBytes(bytes, 19)))
                .setBatteryMicrovoltsPerStep(parseIntFromBytes(bytes, 21, 20))
                .setMessageDeliveryPeriod(parseIntFromBytes(bytes, 23, 22))
                .setMeasurementPeriod(parseIntFromBytes(bytes, 25, 24))
                .setDistanceBetweenAxlesOneTwoMm(parseShortFromBytes(bytes, 27, 26))
                .setDistanceBetweenAxlesTwoThreeMm(parseShortFromBytes(bytes, 29, 28))
                .setDistanceToWheel(parseShortFromBytes(bytes, 31, 30))
                .setConfigType(parseIntFromByte(bytes, 32))
                .setInstallationPoint(parseIntFromByte(bytes, 33))
                .setStateNumber(extractStringFromBytes(bytes, 34, 10))
                .build();
    }

    /**
     * Вычисляет порционный множитель между текущей и предыдущей калибровочной точкой.
     *
     * @param curr Текущая калибровочная точка.
     * @param prev Предыдущая калибровочная точка.
     * @return Значение порционного множителя.
     */
    private static float calculateMultiplier(CalibrationTable curr, CalibrationTable prev) {
        return (curr.getDetector() - prev.getDetector()) * curr.getMultiplier();
    }

    /**
     * Возвращает значение int из одного байта.
     *
     * @param bytes Массив байт.
     * @param index Индекс.
     * @return Целое значение.
     */
    private static int parseIntFromByte(byte[] bytes, int index) {
        return bytes[index] & ZERO_COMMAND_BINARY;
    }

    /**
     * Возвращает значение int из двух байтов.
     *
     * @param bytes  Массив байт.
     * @param index1 Индекс старшего байта.
     * @param index2 Индекс младшего байта.
     * @return Целое значение.
     */
    private static int parseIntFromBytes(byte[] bytes, int index1, int index2) {
        return ((bytes[index1] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[index2] & ZERO_COMMAND_BINARY);
    }

    /**
     * Возвращает значение int из четырёх последовательных байтов.
     * Использует обратный порядок (Big Endian).
     *
     * @param bytes      Массив байт.
     * @param startIndex Индекс последнего (старшего) байта.
     * @return Целое значение.
     */
    private static int parseIntFromBytes(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & ZERO_COMMAND_BINARY) << 24) |
                ((bytes[startIndex - 1] & ZERO_COMMAND_BINARY) << 16) |
                ((bytes[startIndex - 2] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[startIndex - 3] & ZERO_COMMAND_BINARY);
    }

    /**
     * Извлекает значение детектора из массива байт (2 байта).
     *
     * @param bytes Массив байт.
     * @param i     Индекс детектора.
     * @return Значение детектора.
     */
    private static int convertToDetector(byte[] bytes, int i) {
        return ((bytes[i * 6 + 5] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[i * 6 + 4] & ZERO_COMMAND_BINARY);
    }

    /**
     * Извлекает значение множителя из массива байт (4 байта).
     *
     * @param bytes Массив байт.
     * @param i     Индекс множителя.
     * @return Значение множителя.
     */
    private static int convertToMultiplier(byte[] bytes, int i) {
        return ((bytes[i * 6 + 9] & ZERO_COMMAND_BINARY) << 24) |
                ((bytes[i * 6 + 8] & ZERO_COMMAND_BINARY) << 16) |
                ((bytes[i * 6 + 7] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[i * 6 + 6] & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует два байта в значение типа short.
     *
     * @param bytes  Массив байт.
     * @param index1 Старший байт.
     * @param index2 Младший байт.
     * @return Значение типа short.
     */
    private static short parseShortFromBytes(byte[] bytes, int index1, int index2) {
        return (short) (((bytes[index1] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[index2] & ZERO_COMMAND_BINARY));
    }
}
