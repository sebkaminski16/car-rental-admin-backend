package io.github.sebkaminski16.carrentaladmin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "car_models", uniqueConstraints = {
        @UniqueConstraint(name = "uk_model_brand_name", columnNames = {"brand_id", "name"})
})
public class CarModel extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    protected CarModel() {}

    private CarModel(Builder builder) {
        this.name = builder.name;
        this.brand = builder.brand;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public static final class Builder {
        private String name;
        private Brand brand;

        private Builder() {}

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Builder brand(Brand val) {
            this.brand = val;
            return this;
        }

        public CarModel build() {
            return new CarModel(this);
        }
    }
}
