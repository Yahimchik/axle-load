package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_DECIMAL;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDate;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.List;

/**
 * Утилитный класс для работы с байтами, преобразованием чисел и строк.
 */
public class ByteUtils {

    /**
     * Преобразует целое число в 4 байта и записывает их в массив.
     *
     * @param value   массив байт
     * @param intBits целое число
     * @param index   индекс, с которого начинается запись
     */
    public static void intToFourBytes(byte[] value, int intBits, int index) {
        value[index] = (byte) (intBits & ZERO_COMMAND_BINARY);
        value[index + 1] = (byte) ((intBits >> 8) & ZERO_COMMAND_BINARY);
        value[index + 2] = (byte) ((intBits >> 16) & ZERO_COMMAND_BINARY);
        value[index + 3] = (byte) ((intBits >> 24) & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует целое число в 2 байта и записывает их в массив.
     *
     * @param value   массив байт
     * @param intBits целое число
     * @param index   индекс, с которого начинается запись
     */
    public static void intToTwoBytes(byte[] value, int intBits, int index) {
        value[index] = (byte) (intBits & ZERO_COMMAND_BINARY);
        value[index + 1] = (byte) ((intBits >> 8) & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует два байта в целое число.
     */
    public static int convertByteToValue(byte[] bytes, int first, int second) {
        return parseIntFromBytes(bytes, first, second);
    }

    /**
     * Преобразует два байта в целое число.
     */
    public static int convertBytesToValue(byte[] bytes, int first, int second) {
        return (bytes[first] & ZERO_COMMAND_BINARY) * 256 + (bytes[second] & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует один байт в массив байтов.
     */
    public static void intToBytes(byte[] value, int intBits, int index) {
        value[index] = (byte) (intBits & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует строку в байты и записывает в массив по позициям 30–39.
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
     * Преобразует массив байт в список таблиц калибровки.
     *
     * @param bytes массив байт от устройства
     * @param table список, в который будут добавлены элементы
     */
    public static void convertBytesToCalibrationTable(byte[] bytes, List<CalibrationTable> table) {
        if ((bytes[0] & ZERO_COMMAND_BINARY) == FIRST_COMMAND) {
            if ((bytes[1] & ZERO_COMMAND_BINARY) == ZERO_COMMAND_DECIMAL) {
                for (int i = 0; i < 9; i++) {
                    int detector = convertToDetector(bytes, i);

                    if (detector == 0) break;

                    int multiplier = convertToMultiplier(bytes, i);
                    float tableEnd = intToFloat(multiplier);

//                    Log.d("MyTag", String.valueOf(new CalibrationTable(detector, tableEnd)));
                    // TODO: необходимо вставить проверку полноты прочитанных данных
                    table.add(new CalibrationTable(detector, Float.intBitsToFloat(multiplier)));
                    if (tableEnd == 1000000.0F) break;
                }
            }
        }
    }

    /**
     * Извлекает строку из массива байт по индексу.
     *
     * @param data       массив байт
     * @param startIndex начальный индекс
     * @param length     длина строки
     * @return извлечённая строка
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
     */
    public static SensorConfig convertBytesToConfiguration(byte[] bytes) {
        return new SensorConfig.Builder()
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
     * Преобразует массив байт в объект даты устройства.
     */
    public static DeviceDate getDate(byte[] bytes) {
        if (bytes.length == 7) {
            return createDeviceDate(bytes[3], bytes[2], getYearFromTwoBytes(bytes));
        } else if (bytes.length >= 3) {
            return createDeviceDate(bytes[2], bytes[1], bytes[0]);
        }
        return new DeviceDate.Builder().build();
    }

    /**
     * Преобразует int в float, используя побитовую интерпретацию.
     */
    private static float intToFloat(int integer) {
        return Float.intBitsToFloat(integer);
    }

    /**
     * Возвращает int из одного байта.
     */
    private static int parseIntFromByte(byte[] bytes, int index) {
        return bytes[index] & ZERO_COMMAND_BINARY;
    }

    /**
     * Возвращает int из двух байтов, начиная с указанного индекса.
     */
    private static int parseIntFromBytes(byte[] bytes, int index1, int index2) {
        return ((bytes[index1] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[index2] & ZERO_COMMAND_BINARY);
    }

    /**
     * Возвращает int из четырех последовательных байтов, начиная с заданного индекса (в обратном порядке).
     */
    private static int parseIntFromBytes(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & ZERO_COMMAND_BINARY) << 24) |
                ((bytes[startIndex - 1] & ZERO_COMMAND_BINARY) << 16) |
                ((bytes[startIndex - 2] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[startIndex - 3] & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует байты в значение детектора (2 байта).
     */
    private static int convertToDetector(byte[] bytes, int i) {
        return ((bytes[i * 6 + 5] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[i * 6 + 4] & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует байты в значение множителя (4 байта).
     */
    private static int convertToMultiplier(byte[] bytes, int i) {
        return ((bytes[i * 6 + 9] & ZERO_COMMAND_BINARY) << 24) |
                ((bytes[i * 6 + 8] & ZERO_COMMAND_BINARY) << 16) |
                ((bytes[i * 6 + 7] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[i * 6 + 6] & ZERO_COMMAND_BINARY);
    }

    /**
     * Извлекает год из двух байтов (старший и младший).
     */
    private static int getYearFromTwoBytes(byte[] bytes) {
        return ((bytes[1] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[0] & ZERO_COMMAND_BINARY);
    }

    /**
     * Преобразует два байта в short.
     */
    private static short parseShortFromBytes(byte[] bytes, int index1, int index2) {
        return (short) (((bytes[index1] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[index2] & ZERO_COMMAND_BINARY));
    }

    /**
     * Создает объект DeviceDate из отдельных компонентов даты.
     */
    private static DeviceDate createDeviceDate(int year, int month, int day) {
        return new DeviceDate.Builder()
                .addYear(year)
                .addMonth(month)
                .addDay(day)
                .build();
    }

}
