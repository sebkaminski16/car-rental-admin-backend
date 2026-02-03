package io.github.sebkaminski16.carrentaladmin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "brands", uniqueConstraints = {
        @UniqueConstraint(name = "uk_brand_name", columnNames = {"name"})
})
public class Brand extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    protected Brand() {}

    private Brand(Builder builder) {
        this.name = builder.name;
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

    public static final class Builder {
        private String name;

        private Builder() {}

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Brand build() {
            return new Brand(this);
        }
    }
}
