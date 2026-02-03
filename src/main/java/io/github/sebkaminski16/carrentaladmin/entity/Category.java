package io.github.sebkaminski16.carrentaladmin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_category_name", columnNames = {"name"})
})
public class Category extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

     //Discount in percent, e.g. 5.0 = -5% (applied for DAILY rentals)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal dailyDiscountPercent = BigDecimal.ZERO;

     //Discount in percent, e.g. 15.0 = -15% (applied for WEEKLY rentals)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weeklyDiscountPercent = BigDecimal.ZERO;

    protected Category() {}

    private Category(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.dailyDiscountPercent = builder.dailyDiscountPercent != null ? builder.dailyDiscountPercent : BigDecimal.ZERO;
        this.weeklyDiscountPercent = builder.weeklyDiscountPercent != null ? builder.weeklyDiscountPercent : BigDecimal.ZERO;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDailyDiscountPercent() {
        return dailyDiscountPercent;
    }

    public void setDailyDiscountPercent(BigDecimal dailyDiscountPercent) {
        this.dailyDiscountPercent = dailyDiscountPercent;
    }

    public BigDecimal getWeeklyDiscountPercent() {
        return weeklyDiscountPercent;
    }

    public void setWeeklyDiscountPercent(BigDecimal weeklyDiscountPercent) {
        this.weeklyDiscountPercent = weeklyDiscountPercent;
    }

    public static final class Builder {
        private String name;
        private String description;
        private BigDecimal dailyDiscountPercent;
        private BigDecimal weeklyDiscountPercent;

        private Builder() {}

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Builder description(String val) {
            this.description = val;
            return this;
        }

        public Builder dailyDiscountPercent(BigDecimal val) {
            this.dailyDiscountPercent = val;
            return this;
        }

        public Builder weeklyDiscountPercent(BigDecimal val) {
            this.weeklyDiscountPercent = val;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }
}
