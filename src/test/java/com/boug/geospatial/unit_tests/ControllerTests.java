package com.boug.geospatial.unit_tests;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.endpoints.PointOfInterestWs;
import com.boug.geospatial.service.PointOfInterestService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = PointOfInterestWs.class)
public class ControllerTests {

    @MockBean
    PointOfInterestService pointOfInterestService;

    @Autowired
    private ApplicationContext applicationContext;
    private MockWebServiceClient mockClient;

    @Before
    public void createClient() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
        GenericApplicationContext ctx = (GenericApplicationContext) applicationContext;

        final XmlBeanDefinitionReader definitionReader = new XmlBeanDefinitionReader(ctx);
        definitionReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
        definitionReader.setNamespaceAware(false);
    }

    @Test
    public void testGetNearestPoint() {
        GeometryFactory factory = new GeometryFactory();
        Point point = factory.createPoint(new Coordinate( 4.25, 10.8));
        PointOfInterest nearestPointRetrieved = new PointOfInterest(24L, "Test City" , point,
                "Test Country", "admin", null, 232L);
        when(pointOfInterestService.getNearestPointAndUpdateCounter(12.0, 33.0)).thenReturn(nearestPointRetrieved);

        Source requestPayload = new StringSource(
                        "<getNearestPointRequest xmlns='map-points'>" +
                        "         <coordinates>" +
                        "            <lat>12.0</lat>" +
                        "            <lng>33.0</lng>" +
                        "         </coordinates>" +
                        "      </getNearestPointRequest>");
        Source responsePayload = new StringSource(
                "<ns2:getNearestPointResponse xmlns:ns2='map-points'>" +
                        "         <ns2:pointOfInterest>" +
                        "            <ns2:city>Test City</ns2:city>" +
                        "            <ns2:capital>admin</ns2:capital>" +
                        "            <ns2:country>Test Country</ns2:country>" +
                        "            <ns2:population xsi:nil='true' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'/>" +
                        "            <ns2:requestCounter>232</ns2:requestCounter>" +
                        "            <ns2:coordinates>" +
                        "               <ns2:lat>4.25</ns2:lat>" +
                        "               <ns2:lng>10.8</ns2:lng>" +
                        "            </ns2:coordinates>" +
                        "         </ns2:pointOfInterest>" +
                        "      </ns2:getNearestPointResponse>");
        mockClient.sendRequest(withPayload(requestPayload)).andExpect(payload(responsePayload));
    }

    @Test
    public void testGetNearestCityName() {
        when(pointOfInterestService.getNearestCityNameAndUpdateCounter(12.0, 33.0)).thenReturn("Kalamata");

        Source requestPayload = new StringSource(
                "<getNearestCityNameRequest xmlns='map-points'>" +
                        "         <coordinates>" +
                        "            <lat>12.0</lat>" +
                        "            <lng>33.0</lng>" +
                        "         </coordinates>" +
                        "      </getNearestCityNameRequest>");
        Source responsePayload = new StringSource(
                "<ns2:getNearestCityNameResponse xmlns:ns2='map-points'>" +
                        "         <ns2:cityName>Kalamata</ns2:cityName>" +
                        "      </ns2:getNearestCityNameResponse>");
        mockClient.sendRequest(withPayload(requestPayload)).andExpect(payload(responsePayload));
    }

    @Test
    public void testGetPointsWithGreatestCounter() {
        GeometryFactory factory = new GeometryFactory();

        Point point1 = factory.createPoint(new Coordinate( 4.25, 10.8));
        PointOfInterest pointOfInterest1 = new PointOfInterest(24L, "Test City1" , point1,
                "Test Country1", "admin", 3500L, 232L);

        Point point2 = factory.createPoint(new Coordinate( 25.67, 88.8));
        PointOfInterest pointOfInterest2 = new PointOfInterest(3432L, "Test City2" , point2,
                "Test Country2", "admin", 345345L, 22L);

        Point point3 = factory.createPoint(new Coordinate( 11.77, 32.8));
        PointOfInterest pointOfInterest3 = new PointOfInterest(33L, "Test City3" , point3,
                "Test Country3", "admin", 3700000L, 33333L);

        when(pointOfInterestService.getPointsWithGreaterCounter(3)).thenReturn(Arrays.asList(pointOfInterest1,
                pointOfInterest2, pointOfInterest3));

        Source requestPayload = new StringSource(
                "<getPointsWithGreaterCounterRequest xmlns='map-points'>" +
                        "   <request_counter>3</request_counter>" +
                        "</getPointsWithGreaterCounterRequest>");
        Source responsePayload = new StringSource(
                "<ns2:getPointsWithGreaterCounterResponse xmlns:ns2='map-points'>" +
                        "       <ns2:pointOfInterests>" +
                        "            <ns2:city>Test City1</ns2:city>" +
                        "            <ns2:capital>admin</ns2:capital>" +
                        "            <ns2:country>Test Country1</ns2:country>" +
                        "            <ns2:population>3500</ns2:population>" +
                        "            <ns2:requestCounter>232</ns2:requestCounter>" +
                        "            <ns2:coordinates>" +
                        "               <ns2:lat>4.25</ns2:lat>" +
                        "               <ns2:lng>10.8</ns2:lng>" +
                        "            </ns2:coordinates>" +
                        "       </ns2:pointOfInterests>" +
                        "       <ns2:pointOfInterests>" +
                        "            <ns2:city>Test City2</ns2:city>" +
                        "            <ns2:capital>admin</ns2:capital>" +
                        "            <ns2:country>Test Country2</ns2:country>" +
                        "            <ns2:population>345345</ns2:population>" +
                        "            <ns2:requestCounter>22</ns2:requestCounter>" +
                        "            <ns2:coordinates>" +
                        "               <ns2:lat>25.67</ns2:lat>" +
                        "               <ns2:lng>88.8</ns2:lng>" +
                        "            </ns2:coordinates>" +
                        "       </ns2:pointOfInterests>" +
                        "       <ns2:pointOfInterests>" +
                        "            <ns2:city>Test City3</ns2:city>" +
                        "            <ns2:capital>admin</ns2:capital>" +
                        "            <ns2:country>Test Country3</ns2:country>" +
                        "            <ns2:population>3700000</ns2:population>" +
                        "            <ns2:requestCounter>33333</ns2:requestCounter>" +
                        "            <ns2:coordinates>" +
                        "               <ns2:lat>11.77</ns2:lat>" +
                        "               <ns2:lng>32.8</ns2:lng>" +
                        "            </ns2:coordinates>" +
                        "       </ns2:pointOfInterests>" +
                        "      </ns2:getPointsWithGreaterCounterResponse>");

        mockClient.sendRequest(withPayload(requestPayload)).andExpect(payload(responsePayload));
    }
}
