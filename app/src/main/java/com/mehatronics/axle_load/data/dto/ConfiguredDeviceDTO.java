package com.mehatronics.axle_load.data.dto;

import java.util.Objects;

public record ConfiguredDeviceDTO(String mac,String weight, String pressure) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConfiguredDeviceDTO that)) return false;
        return Objects.equals(weight, that.weight) && Objects.equals(pressure, that.pressure);
    }
}
