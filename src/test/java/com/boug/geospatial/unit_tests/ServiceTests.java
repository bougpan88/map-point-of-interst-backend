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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTests {

    @MockBean
    private PointOfInterestRepository pointOfInterestRepository;

    @Before
    public void setUp() {
        when(pointOfInterestRepository.findAll()).thenReturn(createInitialData(1000));
    }

	@Test(expected = IllegalStateException.class)
	public void testOutdatedCache() {
        PointOfInterestService pointOfInterestService = new PointOfInterestService(pointOfInterestRepository, 10);
        pointOfInterestService.init();
        PointOfInterestCache pointOfInterestCache = pointOfInterestService.getNearestCachePointAndUpdateCounter(32.32, 12.342);

        assertNotNull(pointOfInterestCache);
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
