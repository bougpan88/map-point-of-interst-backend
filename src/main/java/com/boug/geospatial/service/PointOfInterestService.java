package com.boug.geospatial.service;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.repository.PointOfInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointOfInterestService {

    private PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    public PointOfInterestService(PointOfInterestRepository pointOfInterestRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    public List<PointOfInterest> getPoints(String city){
        return pointOfInterestRepository.findByCity(city);
    }



}
