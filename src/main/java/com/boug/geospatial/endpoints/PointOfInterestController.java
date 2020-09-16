package com.boug.geospatial.endpoints;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.service.PointOfInterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("points")
public class PointOfInterestController {

    private PointOfInterestService pointOfInterestService;

    @Autowired
    public PointOfInterestController(PointOfInterestService pointOfInterestService){
        this.pointOfInterestService = pointOfInterestService;
    }

    @GetMapping(path = "/{city}")
    public ResponseEntity<List<PointOfInterest>> getCity(@PathVariable String city) {
        return ResponseEntity.ok(pointOfInterestService.getPoints(city));
    }


}
