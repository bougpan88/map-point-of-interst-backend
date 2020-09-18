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

    private String country;

    private String capital;

    private Long population;

    @Column(name = "request_counter")
    private Long requestCounter;

    public PointOfInterest(Long id, @NotNull @Size(min = 1) String city, Point mapPoint, String country, String capital, Long population, Long requestCounter) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Point getMapPoint() {
        return mapPoint;
    }

    public void setMapPoint(Point mapPoint) {
        this.mapPoint = mapPoint;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public Long getRequestCounter() {
        return requestCounter;
    }

    public void setRequestCounter(Long requestCounter) {
        this.requestCounter = requestCounter;
    }
}
