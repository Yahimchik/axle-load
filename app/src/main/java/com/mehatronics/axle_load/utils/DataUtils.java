package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.ByteUtils.convertByteToValue;
import static com.mehatronics.axle_load.utils.ByteUtils.getDate;
import static com.mehatronics.axle_load.utils.DateFormatUtils.convertToDateFormat;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.utils.constants.StringConstants.UNKNOWN;
import static com.mehatronics.axle_load.utils.constants.StringConstants.ZERO;

import com.mehatronics.axle_load.entities.enums.CharacteristicType;

/**
 * Утилитный класс для преобразования байтов в строки, значения батареи,
 * даты и измеренные значения (вес или давление) для axle load устройств.
 */
public class DataUtils {

    /**
     * Преобразует массив байт в строку.
     *
     * @param bytes Массив байт.
     * @return Строковое представление байтов.
     */
    public static String convertBytesToString(byte[] bytes) {
        return new String(bytes);
    }

    /**
     * Преобразует массив байт в строковое значение заряда батареи.
     * Используется только первый байт массива.
     *
     * @param bytes Массив байт (ожидается до 3 байт).
     * @return Значение заряда батареи в виде строки, либо "0", если данные недопустимы.
     */
    public static String convertBytesToBattery(byte[] bytes) {
        if (bytes != null && bytes.length <= 3) {
            return String.valueOf(bytes[0]);
        }
        return ZERO;
    }

    /**
     * Преобразует массив байт в строковое представление даты.
     *
     * @param bytes Массив байт, содержащий дату устройства.
     * @return Отформатированная строка даты или "неизвестно" при ошибке.
     */
    public static String convertBytesToDate(byte[] bytes) {
        if (bytes != null) {
            return convertToDateFormat(getDate(bytes));
        }
        return UNKNOWN;
    }

    /**
     * Преобразует массив байт в строковое значение измеренной характеристики
     * (вес или давление) в зависимости от типа.
     *
     * <p>Проверяются управляющие байты (0-й и 1-й), чтобы удостовериться, что данные валидны.
     *
     * @param bytes Массив байт с данными.
     * @param type Тип характеристики: {@link CharacteristicType#WEIGHT} или {@link CharacteristicType#PRESSURE}.
     * @return Значение характеристики в виде строки или "0" при недопустимых данных.
     */
    public static String convertBytesToValue(byte[] bytes, CharacteristicType type) {
        if ((bytes[0] & ZERO_COMMAND_BINARY) == SEVEN_COMMAND) {
            if ((bytes[1] & ZERO_COMMAND_BINARY) == SECOND_COMMAND) {
                if (type.equals(WEIGHT)) {
                    return String.valueOf(convertByteToValue(bytes, 7, 6));
                }

                if (type.equals(PRESSURE)) {
                    return String.valueOf(convertByteToValue(bytes, 5, 4) / 10f);
                }
            }
        }
        return ZERO;
    }

    /**
     * Преобразует строку давления в целое значение в десятых долях (например, 2.3 → 23).
     *
     * @param pressure Давление в строковом формате.
     * @return Целочисленное представление давления * 10, или 0, если строка пустая или "0".
     */
    public static int parsePressure(String pressure) {
        if (pressure == null || pressure.equalsIgnoreCase(ZERO)) {
            return 0;
        }
        return (int) (Float.parseFloat(pressure) * 10);
    }
}
