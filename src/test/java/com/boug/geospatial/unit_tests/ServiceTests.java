package com.boug.geospatial.unit_tests;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.dto.PointOfInterestCache;
import com.boug.geospatial.repository.PointOfInterestRepository;
import com.boug.geospatial.service.PointOfInterestService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTests {

    @MockBean
    private PointOfInterestRepository pointOfInterestRepository;

    @Before
    public void setUp() {
        reset(pointOfInterestRepository);
    }

	@Test(expected = IllegalStateException.class)
	public void testExistInCacheButNotInDatabase() {
        //This will make sure that during init() our cache has all those objects
        when(pointOfInterestRepository.findAll()).thenReturn(createInitialData(1000));
        PointOfInterestService pointOfInterestService = new PointOfInterestService(pointOfInterestRepository, 10);
        //Load cache
        pointOfInterestService.init();
        //Repository.findById is not mocked so whatever object is the nearest object in cache, will not exist in database.
        //Find by id will return null
        PointOfInterestCache pointOfInterestCache = pointOfInterestService.getNearestCachePointAndUpdateCounter(32.32, 12.342);
        assertNotNull(pointOfInterestCache);
        pointOfInterestService.getNearestPointAndUpdateCounter(32.32, 12.342);
	}

    @Test(expected = IllegalStateException.class)
    public void testInconsistentCoordinates() {

        GeometryFactory factory = new GeometryFactory();
        Point point = factory.createPoint(new Coordinate( 4.25, 10.8));
        PointOfInterest pointOfInterest = new PointOfInterest(24L, "Test City" , point,
                "Test Country", "admin", null, 232L);
        Point changedPoint = factory.createPoint(new Coordinate( 12, 10.8));
        PointOfInterest changedPointOfInterest = new PointOfInterest(24L, "Test City" , changedPoint,
                "Test Country", "admin", null, 232L);

        //This will make sure that during init() our cache has this object
        when(pointOfInterestRepository.findAll()).thenReturn(Collections.singletonList(pointOfInterest));
        when(pointOfInterestRepository.findById(24L)).thenReturn(Optional.of(changedPointOfInterest));
        //As we have only 1 object in cache and Database it must be returned as the closest in all cases
        PointOfInterestService pointOfInterestService = new PointOfInterestService(pointOfInterestRepository, 10);
        //Load cache
        pointOfInterestService.init();
        pointOfInterestService.getNearestPointAndUpdateCounter(32.32, 12.342);
    }

	public static List<PointOfInterest> createInitialData(int size){
        List<PointOfInterest> pointOfInterests = new ArrayList<>();
        GeometryFactory factory = new GeometryFactory();
        for(int i=1; i< size; i++){
            Point point = factory.createPoint(new Coordinate(i + 4.25, i + 10.899));
            long id = i;
            long population = i * 1000;
            PointOfInterest pointOfInterest = new PointOfInterest(id, "City"+i , point, "Country"+i,
                                                          "Capital"+i, population, 0L);
            pointOfInterests.add(pointOfInterest);
        }
        return pointOfInterests;
    }

}
