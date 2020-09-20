package com.boug.geospatial;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.dto.PointOfInterestCache;
import com.boug.geospatial.repository.PointOfInterestRepository;
import com.boug.geospatial.service.PointOfInterestService;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(scripts = "classpath:/initDDL.sql")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {IntegrationTestConfiguration.class})
public class PointOfInterestIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("mdillon/postgis:9.5-alpine")
            .withDatabaseName("integration-tests-db")
            .withUsername("panagiotis")
            .withPassword("password");

    @Autowired
    private PointOfInterestRepository pointOfInterestRepository;

    @Value("${maxCounterUpdateIncreaseAttempts}")
    private Integer maxCounterIncreaseAttempts;


    public PointOfInterestIntegrationTest(){
    }

    @Test
    @Sql(scripts = "classpath:/initDML.sql")
    public void testRepositoryCounterIncrement(){
        pointOfInterestRepository.increaseCounter(1300715560L);
        pointOfInterestRepository.increaseCounter(1300715560L);

        PointOfInterest pointOfInterest = pointOfInterestRepository.findById(1300715560L).get();
        long expectedCounter = 2L;
        long actualCounter = pointOfInterest.getRequestCounter();
        assertEquals(expectedCounter, actualCounter);
    }

    @Test
    @Sql(scripts = "classpath:/initDML.sql")
    public void testServiceFindNearestPoint(){
      //Create Service and load cache in memory
      PointOfInterestService pointOfInterestService = new PointOfInterestService(pointOfInterestRepository, maxCounterIncreaseAttempts);
      pointOfInterestService.init();

      double spartiLat = 37.07161476414343D;
      double spartiLng = 22.425155639648438D;

      double palermoLat = 38.1112268D;
      double palermoLng = 13.3524434D;

      PointOfInterest nearestToSpartiPoint = pointOfInterestService.getNearestPointAndUpdateCounter(spartiLat, spartiLng);
      PointOfInterest nearestToPalermoPoint = pointOfInterestService.getNearestPointAndUpdateCounter(palermoLat, palermoLng);

      assertEquals("Kalamata", nearestToSpartiPoint.getCity());
      assertEquals("Kerkyra", nearestToPalermoPoint.getCity());
    }

    @Test
    @Sql(scripts = "classpath:/initDML.sql")
    public void testServiceFindNearestPointAndIncreaseCounter(){

        //Create Service and load cache in memory
        PointOfInterestService pointOfInterestService = new PointOfInterestService(pointOfInterestRepository, maxCounterIncreaseAttempts);
        pointOfInterestService.init();

        double spartiLat = 37.07161476414343D;
        double spartiLng = 22.425155639648438D;
        Long expectedCounter;

        PointOfInterest nearestPointOfInterest = pointOfInterestService.getNearestPointAndUpdateCounter(spartiLat, spartiLng);
        assertEquals("Kalamata", nearestPointOfInterest.getCity());
        expectedCounter = 1L;
        assertEquals(expectedCounter, nearestPointOfInterest.getRequestCounter());

        nearestPointOfInterest = pointOfInterestService.getNearestPointAndUpdateCounter(spartiLat, spartiLng);
        assertEquals("Kalamata", nearestPointOfInterest.getCity());
        expectedCounter = 2L;
        assertEquals(expectedCounter, nearestPointOfInterest.getRequestCounter());

        nearestPointOfInterest = pointOfInterestService.getNearestPointAndUpdateCounter(spartiLat, spartiLng);
        assertEquals("Kalamata", nearestPointOfInterest.getCity());
        expectedCounter = 3L;
        assertEquals(expectedCounter, nearestPointOfInterest.getRequestCounter());
    }

    @Test
    @Sql(scripts = "classpath:/initDML.sql")
    public void testServiceFindNearestCityNameAndIncreaseCounter(){

        //Create Service and load cache in memory
        PointOfInterestService pointOfInterestService = new PointOfInterestService(pointOfInterestRepository, maxCounterIncreaseAttempts);
        pointOfInterestService.init();

        double spartiLat = 37.07161476414343D;
        double spartiLng = 22.425155639648438D;
        Long expectedCounter;

        PointOfInterestCache nearestPointOfInterestCache = pointOfInterestService.getNearestCachePointAndUpdateCounter(spartiLat, spartiLng);
        assertEquals("Kalamata", nearestPointOfInterestCache.getCityName());
        PointOfInterest nearestPointDB = pointOfInterestRepository.findById(nearestPointOfInterestCache.getId()).get();
        expectedCounter = 1L;
        assertEquals(expectedCounter, nearestPointDB.getRequestCounter());

        nearestPointOfInterestCache = pointOfInterestService.getNearestCachePointAndUpdateCounter(spartiLat, spartiLng);
        assertEquals("Kalamata", nearestPointOfInterestCache.getCityName());
        nearestPointDB = pointOfInterestRepository.findById(nearestPointOfInterestCache.getId()).get();
        expectedCounter = 2L;
        assertEquals(expectedCounter, nearestPointDB.getRequestCounter());

        nearestPointOfInterestCache = pointOfInterestService.getNearestCachePointAndUpdateCounter(spartiLat, spartiLng);
        assertEquals("Kalamata", nearestPointOfInterestCache.getCityName());
        nearestPointDB = pointOfInterestRepository.findById(nearestPointOfInterestCache.getId()).get();
        expectedCounter = 3L;
        assertEquals(expectedCounter, nearestPointDB.getRequestCounter());
    }

    @Test
    @Sql(scripts = "classpath:/initDML.sql")
    public void testServiceFindPointsWithGreaterCounter(){
        //Create Service and load cache in memory
        PointOfInterestService pointOfInterestService = new PointOfInterestService(pointOfInterestRepository, maxCounterIncreaseAttempts);
        pointOfInterestService.init();

        List<PointOfInterest> pointOfInterestList = pointOfInterestService.getPointsWithGreaterCounter(2);
        List<String> pointOfInterestCityNames = pointOfInterestList.stream().map(PointOfInterest::getCity).collect(Collectors.toList());
        List<String> expectedCityNames = Arrays.asList("Patra", "Rodos");

        assertEquals(2, pointOfInterestList.size());
        assertThat(pointOfInterestCityNames, containsInAnyOrder(expectedCityNames.toArray()));
    }

}
