package com.mehatronics.axle_load.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.mehatronics.axle_load.data.dto.ConfiguredDeviceDTO;
import com.mehatronics.axle_load.data.mapper.ConfiguredDeviceMapper;
import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.data.repository.impl.BluetoothRepository;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.domain.entities.device.Device;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class AxisDevicesStateCombiner {
    private final MediatorLiveData<List<AxisUiModel>> uiAxisModels = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> allDevicesSaved = new MediatorLiveData<>();
    private final BluetoothRepository bluetoothRepository;
    private final DeviceRepository deviceRepository;
    private final ConfiguredDeviceMapper deviceMapper;

    @Inject
    public AxisDevicesStateCombiner(
            DeviceRepository deviceRepository,
            BluetoothRepository bluetoothRepository,
            ConfiguredDeviceMapper deviceMapper
    ) {
        this.deviceRepository = deviceRepository;
        this.bluetoothRepository = bluetoothRepository;
        this.deviceMapper = deviceMapper;
        addSource();
    }

    public LiveData<List<AxisUiModel>> getUiAxisModels() {
        return uiAxisModels;
    }

    public LiveData<Boolean> getAllDevicesSaved() {
        return allDevicesSaved;
    }

    private void addSource() {
        allDevicesSaved.addSource(deviceRepository.getAxisList(), list -> checkAllSaved());
        allDevicesSaved.addSource(deviceRepository.getConfiguredMacs(), macs -> checkAllSaved());

        uiAxisModels.addSource(deviceRepository.getAxisList(), axisList
                -> parse(axisList, bluetoothRepository.getScannedDevices().getValue()));
        uiAxisModels.addSource(bluetoothRepository.getScannedDevices(), devices
                -> parse(deviceRepository.getAxisList().getValue(), devices));
    }

    private void parse(List<AxisModel> axisList, List<Device> scannedDevices) {
        if (axisList == null || scannedDevices == null) return;

        List<ConfiguredDeviceDTO> dtos = scannedDevices.stream()
                .map(deviceMapper::convertToConfiguredDevice)
                .collect(Collectors.toList());

        List<AxisUiModel> uiModels = axisList.stream()
                .map(axis -> deviceMapper.toUiModel(axis, dtos))
                .collect(Collectors.toList());

        uiAxisModels.setValue(uiModels);
    }

    private void checkAllSaved() {
        List<AxisModel> axes = deviceRepository.getAxisList().getValue();
        Set<String> finished = deviceRepository.getConfiguredMacs().getValue();

        if (axes == null || finished == null) {
            allDevicesSaved.setValue(false);
            return;
        }

        Set<String> selectedMacs = axes.stream()
                .flatMap(axis -> axis.getSideDeviceMap().values().stream())
                .filter(mac -> mac != null && !mac.isEmpty())
                .collect(Collectors.toSet());

        boolean saved = !axes.isEmpty()
                && axes.stream().allMatch(axis ->
                axis.getSideDeviceMap().values().stream().anyMatch(mac -> mac != null && !mac.isEmpty())
        )
                && finished.containsAll(selectedMacs)
                && selectedMacs.containsAll(finished);

        allDevicesSaved.setValue(saved);
    }
}