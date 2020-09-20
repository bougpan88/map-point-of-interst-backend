package com.boug.geospatial.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.vividsolutions.jts.geom.Point;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "point_of_interest")
@Getter
@Setter
@NoArgsConstructor
public class PointOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 1)
    private String city;

    @NotNull
    private Point mapPoint;

    @Column(name = "request_counter")
    @NotNull
    private Long requestCounter;

    private String country;

    private String capital;

    private Long population;

    public PointOfInterest(Long id, @NotNull @Size(min = 1) String city, Point mapPoint, String country, String capital, Long population,@NotNull Long requestCounter) {
        this.id = id;
        this.city = city;
        this.mapPoint = mapPoint;
        this.country = country;
        this.capital = capital;
        this.population = population;
        this.requestCounter = requestCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointOfInterest that = (PointOfInterest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", mapPoint=" + mapPoint +
                ", country='" + country + '\'' +
                ", capital='" + capital + '\'' +
                ", population=" + population +
                ", requestCounter=" + requestCounter +
                '}';
    }
}
