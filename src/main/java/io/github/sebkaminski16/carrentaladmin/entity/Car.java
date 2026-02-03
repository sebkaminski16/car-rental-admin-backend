package io.github.sebkaminski16.carrentaladmin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cars", uniqueConstraints = {
        @UniqueConstraint(name = "uk_car_vin", columnNames = {"vin"}),
        @UniqueConstraint(name = "uk_car_license_plate", columnNames = {"licensePlate"})
})
public class Car extends BaseEntity {

    @Column(nullable = false, length = 32)
    private String vin;

    @Column(nullable = false, length = 16)
    private String licensePlate;

    @Column(nullable = false)
    private Integer productionYear;

    @Column(length = 40)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CarStatus status = CarStatus.AVAILABLE;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 512)
    private String imageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weeklyRate;

    @Column(nullable = false)
    private Integer mileageKm = Integer.valueOf(0);

    protected Car() {}

    private Car(Builder builder) {
        this.vin = builder.vin;
        this.licensePlate = builder.licensePlate;
        this.productionYear = builder.productionYear;
        this.color = builder.color;
        this.status = builder.status != null ? builder.status : CarStatus.AVAILABLE;
        this.model = builder.model;
        this.category = builder.category;
        this.imageUrl = builder.imageUrl;
        this.hourlyRate = builder.hourlyRate;
        this.dailyRate = builder.dailyRate;
        this.weeklyRate = builder.weeklyRate;
        this.mileageKm = Integer.valueOf(builder.mileageKm != null ? builder.mileageKm : 0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public CarModel getModel() {
        return model;
    }

    public void setModel(CarModel model) {
        this.model = model;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public BigDecimal getWeeklyRate() {
        return weeklyRate;
    }

    public void setWeeklyRate(BigDecimal weeklyRate) {
        this.weeklyRate = weeklyRate;
    }

    public Integer getMileageKm() {
        return mileageKm;
    }

    public void setMileageKm(Integer mileageKm) {
        this.mileageKm = mileageKm;
    }

    public static final class Builder {
        private String vin;
        private String licensePlate;
        private Integer productionYear;
        private String color;
        private CarStatus status;
        private CarModel model;
        private Category category;
        private String imageUrl;
        private BigDecimal hourlyRate;
        private BigDecimal dailyRate;
        private BigDecimal weeklyRate;
        private Integer mileageKm;

        private Builder() {}

        public Builder vin(String val) {
            this.vin = val;
            return this;
        }

        public Builder licensePlate(String val) {
            this.licensePlate = val;
            return this;
        }

        public Builder productionYear(Integer val) {
            this.productionYear = val;
            return this;
        }

        public Builder color(String val) {
            this.color = val;
            return this;
        }

        public Builder status(CarStatus val) {
            this.status = val;
            return this;
        }

        public Builder model(CarModel val) {
            this.model = val;
            return this;
        }

        public Builder category(Category val) {
            this.category = val;
            return this;
        }

        public Builder imageUrl(String val) {
            this.imageUrl = val;
            return this;
        }

        public Builder hourlyRate(BigDecimal val) {
            this.hourlyRate = val;
            return this;
        }

        public Builder dailyRate(BigDecimal val) {
            this.dailyRate = val;
            return this;
        }

        public Builder weeklyRate(BigDecimal val) {
            this.weeklyRate = val;
            return this;
        }

        public Builder mileageKm(Integer val) {
            this.mileageKm = val;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }
}
