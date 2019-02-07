package org.tudelft.blockchain.booking.otawebapp.model;

import java.time.LocalDate;

public class Booking {
    private long id;
    private String otaName;
    private String propertyName;
    private LocalDate startDate;
    private LocalDate endDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOtaName() {
        return otaName;
    }

    public void setOtaName(String otaName) {
        this.otaName = otaName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean overbookingAttempt(Booking booking) {
        boolean a  = (booking.getStartDate().isAfter(this.getStartDate()) || booking.getStartDate().isEqual(this.getStartDate()))
                && booking.getStartDate().isBefore(this.getEndDate());

        boolean b = booking.getStartDate().isBefore(this.getStartDate()) && booking.getEndDate().isAfter(this.getStartDate());
        return a || b;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", otaName='" + otaName + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
