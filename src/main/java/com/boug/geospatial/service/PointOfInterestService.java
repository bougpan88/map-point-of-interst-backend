package com.boug.geospatial.service;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.dto.PointOfInterestCache;
import com.boug.geospatial.repository.PointOfInterestRepository;
import com.vividsolutions.jts.geom.Point;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PointOfInterestService {

    private PointOfInterestRepository pointOfInterestRepository;
    private List<PointOfInterestCache> pointOfInterestsCache = new ArrayList<>();
    private static final Logger LOGGER = getLogger(PointOfInterestService.class);
    private Integer maxCounterIncreaseAttempts;

    @Autowired
    public PointOfInterestService(PointOfInterestRepository pointOfInterestRepository,
                                  @Value("${maxCounterUpdateIncreaseAttempts}") Integer maxCounterIncreaseAttempts){
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.maxCounterIncreaseAttempts = maxCounterIncreaseAttempts;
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
     * In case getNearestPoint() is triggered at the same time from another thread(user's request), it won't cause any error.
     * Why are we safe:
     * -If getNearestPoint has gone inside the for loop it will already have an iterator so it will continue iterating through
     * the old list even if this method here (loadDataInMemory) attaches a new list to reference pointOfInterestsCache.
     * The iterator will still point to the original object even if the pointOfInterestsCache reference links to new object.
     * -If getNearestPoint has not gone inside the for loop yet, it will be able to see the new list that has been attached
     * to reference pointOfInterestsCache from this method here (loadDataInMemory).
     */
    @Scheduled(cron ="0 0 23 * * *")
    private void loadDataInMemory(){
        pointOfInterestsCache = pointOfInterestRepository.findAll()
                                                         .stream()
                                                         .map(pointOfInterest -> new PointOfInterestCache(pointOfInterest.getId(),
                                                                                                     pointOfInterest.getMapPoint(),
                                                                                                     pointOfInterest.getCity()))
                                                         .collect(Collectors.toList());
    }

    /**
     * The nearest point is located from cache and then the counter for that row in database is increased by 1.
     * Based on the id of the nearest point that we found in cache we hit database to retrieve full information about
     * this point.
     * @param lat based on this lat we search to find the nearest point in cache
     * @param lng based on this lng we search to find the nearest point in cache
     * @return Full information about the nearest point which is retrieved from database.
     */
    public PointOfInterest getNearestPointAndUpdateCounter(double lat, double lng){
        PointOfInterestCache nearestCachePoint = getNearestCachePointAndUpdateCounter(lat, lng);
        Optional<PointOfInterest> nearestPointDB = pointOfInterestRepository.findById(nearestCachePoint.getId());
        if (nearestPointDB.isPresent() && pointsAreEqual(nearestCachePoint.getMapPoint(), nearestPointDB.get().getMapPoint())){
            return nearestPointDB.get();
        } else {
            LOGGER.error("Cache is outdated for row with id: {}", nearestCachePoint.getId());
            throw new IllegalStateException("Cache is outdated for row with id:" + nearestCachePoint.getId());
        }
    }

    /**
     * The nearest point is located from cache and then the counter for that row in database is increased by 1.
     * This function returns the city name from the nearest point which was found in cache. It doesn't hit database
     * to retrieve info, only to update the counter.
     *
     * @param lat based on this lat we search to find the nearest point in cache
     * @param lng based on this lng we search to find the nearest point in cache
     * @return City name of nearest point directly retrieved from cache.
     */
    public String getNearestCityNameAndUpdateCounter(double lat, double lng){
        return getNearestCachePointAndUpdateCounter(lat, lng).getCityName();
    }

    /**
     * The nearest point is located from cache and then the counter for that row in database is increased by 1.
     * This function returns the nearest point which was found in cache. It doesn't hit database
     * to retrieve info, only to update the counter.
     *
     * @param lat based on this lat we search to find the nearest point in cache
     * @param lng based on this lng we search to find the nearest point in cache
     * @return Nearest point directly retrieved from cache.
     */
    public PointOfInterestCache getNearestCachePointAndUpdateCounter(double lat, double lng){
        PointOfInterestCache nearestPoint = getNearestPoint(lat, lng);
        tryIncrementCounter(nearestPoint.getId());
        return nearestPoint;
    }

    /**
     * Because of REPEATABLE_READ transaction level it is possible that the update fails.
     * We will repeat the update statement until it doesn't fail and maximum maxCounterIncreaseAttempts times.
     * After maxCounterIncreaseAttempts times of failed update attempts we will stop trying.
     */
    private void tryIncrementCounter(long id){
        for (int attempt = 1; attempt <= maxCounterIncreaseAttempts; attempt++) {
            try {
                pointOfInterestRepository.increaseCounter(id);
                if (attempt > 1){
                    LOGGER.info("With attempt {} update for id {} was successful", attempt, id);
                }
                break;
            } catch (RuntimeException ex) {
                LOGGER.error("Update statement for id {} failed. Attempt {}", id, attempt, ex);
            }
        }
    }

    public List<PointOfInterest> getPointsWithGreaterCounter(long counter){
        return pointOfInterestRepository.findByRequestCounterGreaterThan(counter);
    }

    /**
     * This function here searches all objects in cache to find the point with the closest distance to the provided
     * coordinates(lat,lng).
     *
     * O(n). Search and return of point with minimum distance happens in one loop.
     */
    private PointOfInterestCache getNearestPoint(double lat, double lng){
        PointOfInterestCache nearestPoint = null;
        Double minDistance = null;
        for (PointOfInterestCache pointOfInterest : pointOfInterestsCache){
            Double distance = distFrom(lat, lng, pointOfInterest.getMapPoint().getX(), pointOfInterest.getMapPoint().getY());
            if (minDistance == null || distance < minDistance){
                minDistance = distance;
                nearestPoint = pointOfInterest;
            }
        }
        return nearestPoint;
    }

    /**
     * This function here calculates the distance between 2 points (lat1,lng1) and (lat2,lng2).
     */
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

    private static boolean pointsAreEqual(Point pointA, Point pointB){
        return (pointA.getX() == pointB.getX() && pointA.getY() == pointB.getY());
    }

}
