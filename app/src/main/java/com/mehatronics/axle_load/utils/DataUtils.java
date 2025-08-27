package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.constants.StringConstants.ZERO;
import static com.mehatronics.axle_load.domain.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.domain.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.ByteUtils.convertByteToValue;

import com.mehatronics.axle_load.domain.entities.enums.CharacteristicType;

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
     * Преобразует массив байт в строковое значение измеренной характеристики
     * (вес или давление) в зависимости от типа.
     *
     * <p>Проверяются управляющие байты (0-й и 1-й), чтобы удостовериться, что данные валидны.
     *
     * @param bytes Массив байт с данными.
     * @param type  Тип характеристики: {@link CharacteristicType#WEIGHT} или {@link CharacteristicType#PRESSURE}.
     * @return Значение характеристики в виде строки или "0" при недопустимых данных.
     */
    public static String convertBytesToValue(byte[] bytes, CharacteristicType type) {
        if ((bytes[0] & ZERO_COMMAND_BINARY) == SEVEN_COMMAND) {
            if ((bytes[1] & ZERO_COMMAND_BINARY) == SECOND_COMMAND) {
                if (type.equals(WEIGHT)) {
                    return String.valueOf(convertByteToValue(bytes, 7, 6));
                } else if (type.equals(PRESSURE)) {
                    return String.valueOf(convertByteToValue(bytes, 5, 4) / 10f);
                } else {
                    return String.valueOf(bytes[12]);
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