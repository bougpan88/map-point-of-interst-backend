package com.boug.geospatial.endpoints;

import com.boug.geospatial.service.PointOfInterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("points")
public class PointOfInterestController {

    private PointOfInterestService pointOfInterestService;

    @Autowired
    public PointOfInterestController(PointOfInterestService pointOfInterestService){
        this.pointOfInterestService = pointOfInterestService;
    }

    @GetMapping(path = "/{lat}/{lng}")
    public ResponseEntity getNearestPoint(@PathVariable float lat,@PathVariable float lng){
        return ResponseEntity.ok(pointOfInterestService.getNearestPointAndUpdateCounter(lat, lng));
    }


}
