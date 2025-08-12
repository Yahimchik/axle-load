package com.mehatronics.axle_load.constants;

import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.DPS;

import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

import java.util.Map;
import java.util.UUID;

public class UuidConstants {
    public static final UUID USER_SERVICE_DPS = UUID.fromString("58c2f7bf-fef8-4b04-8850-a820113120ad");
    public static final UUID WRITE_CHARACTERISTIC_DPS = UUID.fromString("027dd8e6-3310-49dd-a767-444de694117b");
    public static final UUID READ_CHARACTERISTIC_DPS = UUID.fromString("83940e89-e38d-4093-ba21-ce6aed75ff1c");

    public static final UUID USER_SERVICE_BT_COM_MINI = UUID.fromString("b86176da-98ab-4974-83c1-ca76622464d3");
    public static final UUID WRITE_CHARACTERISTIC_BT_COM_MINI = UUID.fromString("daf19613-ba44-4e63-ae6f-35cadcab96a6");
    public static final UUID READ_CHARACTERISTIC_BT_COM_MINI = UUID.fromString("02bfcf7d-6416-4855-98d3-9174e6da3bf1");

    public static final Map<DeviceType, UUID[]> UUID_MAP = Map.of(
            DPS, new UUID[]{USER_SERVICE_DPS, WRITE_CHARACTERISTIC_DPS, READ_CHARACTERISTIC_DPS},
            BT_COM_MINI, new UUID[]{USER_SERVICE_BT_COM_MINI, WRITE_CHARACTERISTIC_BT_COM_MINI, READ_CHARACTERISTIC_BT_COM_MINI}
    );
}
