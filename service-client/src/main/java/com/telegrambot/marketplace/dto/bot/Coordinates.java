package com.telegrambot.marketplace.dto.bot;

public class Coordinates {

    private double latitude;
    private double longitude;

    // Constants for validation
    public static final double MIN_LATITUDE = -90.0;
    public static final double MAX_LATITUDE = 90.0;
    public static final double MIN_LONGITUDE = -180.0;
    public static final double MAX_LONGITUDE = 180.0;

    // Constructor
    public Coordinates(final double latitude, final double longitude) {
        if (!isValidLatitude(latitude)) {
            throw new IllegalArgumentException("Invalid latitude: " + latitude);
        }
        if (!isValidLongitude(longitude)) {
            throw new IllegalArgumentException("Invalid longitude: " + longitude);
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Setters with validation
    public void setLatitude(final double latitude) {
        if (!isValidLatitude(latitude)) {
            throw new IllegalArgumentException("Invalid latitude: " + latitude);
        }
        this.latitude = latitude;
    }

    public void setLongitude(final double longitude) {
        if (!isValidLongitude(longitude)) {
            throw new IllegalArgumentException("Invalid longitude: " + longitude);
        }
        this.longitude = longitude;
    }

    // Validation methods
    private boolean isValidLatitude(final double latitude) {
        return latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE;
    }

    private boolean isValidLongitude(final double longitude) {
        return longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
