package com.boug.geospatial.model;

import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PointOfInterestCache {

    private Long id;
    private Point mapPoint;
    private String cityName;

    public PointOfInterestCache(Long id, Point mapPoint, String cityName) {
        this.id = id;
        this.mapPoint = mapPoint;
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return "PointOfInterestCache{" +
                "id=" + id +
                ", mapPoint=" + mapPoint +
                ", cityName='" + cityName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointOfInterestCache that = (PointOfInterestCache) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
