package com.boug.geospatial.repository;

import com.boug.geospatial.domain.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {

    List<PointOfInterest> findByCity(String city);


}
