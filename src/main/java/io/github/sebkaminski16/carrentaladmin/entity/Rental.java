package io.github.sebkaminski16.carrentaladmin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime plannedEndAt;

    @Column
    private LocalDateTime actualReturnAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RateType rateType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RentalStatus status = RentalStatus.ACTIVE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(length = 255)
    private String notes;

    protected Rental() {}

    private Rental(Builder builder) {
        this.customer = builder.customer;
        this.car = builder.car;
        this.startAt = builder.startAt;
        this.plannedEndAt = builder.plannedEndAt;
        this.actualReturnAt = builder.actualReturnAt;
        this.rateType = builder.rateType;
        this.status = builder.status != null ? builder.status : RentalStatus.ACTIVE;
        this.basePrice = builder.basePrice != null ? builder.basePrice : BigDecimal.ZERO;
        this.lateFee = builder.lateFee != null ? builder.lateFee : BigDecimal.ZERO;
        this.totalPrice = builder.totalPrice != null ? builder.totalPrice : BigDecimal.ZERO;
        this.notes = builder.notes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getPlannedEndAt() {
        return plannedEndAt;
    }

    public void setPlannedEndAt(LocalDateTime plannedEndAt) {
        this.plannedEndAt = plannedEndAt;
    }

    public LocalDateTime getActualReturnAt() {
        return actualReturnAt;
    }

    public void setActualReturnAt(LocalDateTime actualReturnAt) {
        this.actualReturnAt = actualReturnAt;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static final class Builder {
        private Customer customer;
        private Car car;
        private LocalDateTime startAt;
        private LocalDateTime plannedEndAt;
        private LocalDateTime actualReturnAt;
        private RateType rateType;
        private RentalStatus status;
        private BigDecimal basePrice;
        private BigDecimal lateFee;
        private BigDecimal totalPrice;
        private String notes;

        private Builder() {}

        public Builder customer(Customer val) {
            this.customer = val;
            return this;
        }

        public Builder car(Car val) {
            this.car = val;
            return this;
        }

        public Builder startAt(LocalDateTime val) {
            this.startAt = val;
            return this;
        }

        public Builder plannedEndAt(LocalDateTime val) {
            this.plannedEndAt = val;
            return this;
        }

        public Builder actualReturnAt(LocalDateTime val) {
            this.actualReturnAt = val;
            return this;
        }

        public Builder rateType(RateType val) {
            this.rateType = val;
            return this;
        }

        public Builder status(RentalStatus val) {
            this.status = val;
            return this;
        }

        public Builder basePrice(BigDecimal val) {
            this.basePrice = val;
            return this;
        }

        public Builder lateFee(BigDecimal val) {
            this.lateFee = val;
            return this;
        }

        public Builder totalPrice(BigDecimal val) {
            this.totalPrice = val;
            return this;
        }

        public Builder notes(String val) {
            this.notes = val;
            return this;
        }

        public Rental build() {
            return new Rental(this);
        }
    }
}
