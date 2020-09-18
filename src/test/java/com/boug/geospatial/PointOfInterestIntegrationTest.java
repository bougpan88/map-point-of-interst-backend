package com.boug.geospatial;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.repository.PointOfInterestRepository;
import com.boug.geospatial.service.PointOfInterestService;
import org.junit.Assert;
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
    PointOfInterestRepository pointOfInterestRepository;

    @Value("${maxCounterUpdateIncreaseAttempts}")
    Integer maxCounterIncreaseAttempts;


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
        Assert.assertEquals(expectedCounter, actualCounter);
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

      Assert.assertEquals("Kalamata", nearestToSpartiPoint.getCity());
      Assert.assertEquals("Kerkyra", nearestToPalermoPoint.getCity());
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

        PointOfInterest nearestPointOfInterest = searchCacheFirstAndRetrieveRowFromDB(spartiLat, spartiLng, pointOfInterestService);
        Assert.assertEquals("Kalamata", nearestPointOfInterest.getCity());
        expectedCounter = 1L;
        Assert.assertEquals(expectedCounter, nearestPointOfInterest.getRequestCounter());

        nearestPointOfInterest = searchCacheFirstAndRetrieveRowFromDB(spartiLat, spartiLng, pointOfInterestService);
        Assert.assertEquals("Kalamata", nearestPointOfInterest.getCity());
        expectedCounter = 2L;
        Assert.assertEquals(expectedCounter, nearestPointOfInterest.getRequestCounter());

        nearestPointOfInterest = searchCacheFirstAndRetrieveRowFromDB(spartiLat, spartiLng, pointOfInterestService);
        Assert.assertEquals("Kalamata", nearestPointOfInterest.getCity());
        expectedCounter = 3L;
        Assert.assertEquals(expectedCounter, nearestPointOfInterest.getRequestCounter());
    }

    /**
     * This method uses cache to find the nearest point. Then it uses the id of the cache object to retrieve the actual
     * object from database that has updated values for request_counter
     *
     * @param lat The lat for which we want to find the nearest point
     * @param lng The lng for which we want to find the nearest point
     * @param pointOfInterestService The Service must be passed as parameter as it is locally created in this test
     * @return PointOfInterest The nearest point found from Database
     */
    private PointOfInterest searchCacheFirstAndRetrieveRowFromDB(double lat, double lng, PointOfInterestService pointOfInterestService){
        //This finds the nearest object from cache.
        PointOfInterest nearestPointCacheObject = pointOfInterestService.getNearestPointAndUpdateCounter(lat, lng);
        //Now the retrieved from database object will have reliable data for request_counter
        return pointOfInterestRepository.findById(nearestPointCacheObject.getId()).get();
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

        Assert.assertEquals(2, pointOfInterestList.size());
        assertThat(pointOfInterestCityNames, containsInAnyOrder(expectedCityNames.toArray()));
    }
}
