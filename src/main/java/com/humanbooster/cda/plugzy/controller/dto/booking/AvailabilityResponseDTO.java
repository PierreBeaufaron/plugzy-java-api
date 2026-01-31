package com.humanbooster.cda.plugzy.controller.dto.booking;

public class AvailabilityResponseDTO {

    private boolean available;

    public AvailabilityResponseDTO() {}

    public AvailabilityResponseDTO(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
