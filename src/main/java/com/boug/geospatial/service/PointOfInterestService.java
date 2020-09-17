package com.boug.geospatial.service;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.repository.PointOfInterestRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PointOfInterestService {

    private PointOfInterestRepository pointOfInterestRepository;
    private List<PointOfInterest> pointOfInterests = new ArrayList<>();
    private static final Logger LOGGER = getLogger(PointOfInterestService.class);

    @Value("${maxCounterUpdateIncreaseAttempts}")
    private Integer maxCounterIncreaseAttempts;

    @Autowired
    public PointOfInterestService(PointOfInterestRepository pointOfInterestRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    /**
     * Cache will be loaded in memory on startup while this singleton is created from spring.
     */
    @PostConstruct
    public void init(){
        loadDataInMemory();
    }

    /**
     * Cache will be renewed every day at 23:00
     *
     * In case getNearestPoint is triggered at the same time from another thread, it won't cause any error.
     * Why are we safe:
     * -If getNearestPoint has gone inside the for loop it will already have an iterator so it will continue iterating through
     * the old list even if this method here (loadDataInMemory) attaches a new list to reference pointOfInterests.
     * The iterator will still point to the original object even if the pointOfInterests reference links to new object.
     * -If getNearestPoint has not gone inside the for loop yet, it will be able to see the new list that has been attached
     * to reference pointOfInterests from this method here (loadDataInMemory).
     */
    @Scheduled(cron ="0 0 23 * * *")
    private void loadDataInMemory(){
        pointOfInterests = pointOfInterestRepository.findAll();
    }

    /**
     * The nearest point is retrieved from cache and then the counter for that row in database is increased by 1.
     * Because of REPEATABLE_READ transaction level it is possible that the update fails.
     * We will repeat the update statement until it doesn't fail and maximum maxCounterIncreaseAttempts times.
     * After maxCounterIncreaseAttempts times of failed update attempts we will stop trying.
     */
    public PointOfInterest getNearestPointAndUpdateCounter(double lat, double lng){
        PointOfInterest nearestPoint = getNearestPoint(lat, lng);
        for (int attempt = 1; attempt <= maxCounterIncreaseAttempts; attempt++) {
            try {
                pointOfInterestRepository.increaseCounter(nearestPoint.getId());
                if (attempt > 1){
                    LOGGER.info("With attempt {} update for id {} was successful", attempt, nearestPoint.getId());
                }
                break;
            } catch (RuntimeException ex) {
                LOGGER.error("Update statement for id {} failed. Attempt {}", nearestPoint.getId(), attempt, ex);
            }
        }
        return nearestPoint;
    }

    /**
     * O(n). Search and return of point with minimum distance happens in one loop
     */
    private PointOfInterest getNearestPoint(double lat, double lng){
        PointOfInterest nearestPoint = null;
        Double minDistance = null;
        for (PointOfInterest pointOfInterest : pointOfInterests){
            Double distance = distFrom(lat, lng, pointOfInterest.getMapPoint().getX(), pointOfInterest.getMapPoint().getY());
            if (minDistance == null || distance < minDistance){
                minDistance = distance;
                nearestPoint = pointOfInterest;
            }
        }
        return nearestPoint;
    }

    private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (earthRadius * c);
    }

}
