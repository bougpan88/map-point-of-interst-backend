package com.boug.geospatial.endpoints;

import com.boug.geospatial.service.PointOfInterestService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.slf4j.LoggerFactory.getLogger;

@RestController()
@RequestMapping("points")
public class PointOfInterestController {

    private PointOfInterestService pointOfInterestService;
    private static final Logger LOGGER = getLogger(PointOfInterestController.class);

    @Autowired
    public PointOfInterestController(PointOfInterestService pointOfInterestService){
        this.pointOfInterestService = pointOfInterestService;
    }

    @GetMapping(path = "/{lat}/{lng}")
    public ResponseEntity getNearestPoint(@PathVariable float lat,@PathVariable float lng){
        return ResponseEntity.ok(pointOfInterestService.getNearestPointAndUpdateCounter(lat, lng));
    }

    @GetMapping(path = "city/{lat}/{lng}")
    public ResponseEntity getNearestCityName(@PathVariable float lat,@PathVariable float lng){
        return ResponseEntity.ok(pointOfInterestService.getNearestCityNameAndUpdateCounter(lat, lng));
    }


}
