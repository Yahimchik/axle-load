package com.mehatronics.axle_load.data.dto;

import androidx.annotation.NonNull;

import com.mehatronics.axle_load.domain.entities.device.Device;

import java.util.Objects;

public record DeviceResponseDTO(
        String name,
        String mac,
        String rssi,
        String weight,
        String pressure,
        Device originalDevice,
        String battery,
        String stateNumber,
        String installationPoint
) {

    public DeviceResponseDTO() {
        this("", "", "", "", "", null, "", "", "");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DeviceResponseDTO that)) return false;
        return Objects.equals(name, that.name)
                && Objects.equals(mac, that.mac)
                && Objects.equals(rssi, that.rssi)
                && Objects.equals(weight, that.weight)
                && Objects.equals(pressure, that.pressure)
                && Objects.equals(originalDevice, that.originalDevice);
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceResponseDTO[" +
                "name=" + name + ", " +
                "mac=" + mac + ", " +
                "rssi=" + rssi + ", " +
                "weight=" + weight + ", " +
                "pressure=" + pressure + ", " +
                "originalDevice=" + originalDevice + ", " +
                "battery=" + battery + ", " +
                "stateNumber=" + stateNumber + ", " +
                "installationPoint=" + installationPoint + ']';
    }
}
