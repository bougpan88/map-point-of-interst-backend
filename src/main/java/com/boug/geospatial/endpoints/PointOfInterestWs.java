package com.boug.geospatial.endpoints;

import com.boug.geospatial.domain.PointOfInterest;
import com.boug.geospatial.dto.generated_soap_dtos.*;
import com.boug.geospatial.service.PointOfInterestService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Endpoint
public class PointOfInterestWs {

    private static final String NAMESPACE_URI = "map-points";
    private static final Logger LOGGER = getLogger(PointOfInterestWs.class);
    private PointOfInterestService pointOfInterestService;

    @Autowired
    public PointOfInterestWs(PointOfInterestService pointOfInterestService){
        this.pointOfInterestService = pointOfInterestService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNearestPointRequest")
    @ResponsePayload
    public GetNearestPointResponse getNearestPoint(@RequestPayload GetNearestPointRequest getNearestPointRequest) {
        GetNearestPointResponse response = new GetNearestPointResponse();
        response.setPointOfInterest(mapToPointOfInterestDto(pointOfInterestService.getNearestPointAndUpdateCounter(getNearestPointRequest.getCoordinates().getLat(),
                getNearestPointRequest.getCoordinates().getLng())));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNearestCityNameRequest")
    @ResponsePayload
    public GetNearestCityNameResponse getNearestCityName(@RequestPayload GetNearestCityNameRequest getNearestCityNameRequest){
        GetNearestCityNameResponse response = new GetNearestCityNameResponse();
        response.setCityName(pointOfInterestService.getNearestCityNameAndUpdateCounter(getNearestCityNameRequest.getCoordinates().getLat(),
                getNearestCityNameRequest.getCoordinates().getLng()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPointsWithGreaterCounterRequest")
    @ResponsePayload
    public GetPointsWithGreaterCounterResponse getPointsWithGreaterCounter(@RequestPayload GetPointsWithGreaterCounterRequest getPointsWithGreaterCounterRequest){
        GetPointsWithGreaterCounterResponse response = new GetPointsWithGreaterCounterResponse();
        List<PointOfInterest> pointOfInterestDtoList = pointOfInterestService.getPointsWithGreaterCounter(getPointsWithGreaterCounterRequest.getRequestCounter());
        List<PointOfInterestDto> pointOfInterestDtos = pointOfInterestDtoList.stream().map(PointOfInterestWs::mapToPointOfInterestDto).collect(Collectors.toList());
        response.getPointOfInterests().addAll(pointOfInterestDtos);
        return response;
    }

    private static PointOfInterestDto mapToPointOfInterestDto(PointOfInterest pointOfInterest) {
        PointOfInterestDto pointOfInterestDto = new PointOfInterestDto();
        pointOfInterestDto.setCapital(pointOfInterest.getCapital());
        pointOfInterestDto.setCity(pointOfInterest.getCity());
        pointOfInterestDto.setCountry(pointOfInterest.getCountry());
        pointOfInterestDto.setPopulation(pointOfInterest.getPopulation());
        pointOfInterestDto.setRequestCounter(pointOfInterest.getRequestCounter());
        pointOfInterestDto.setCoordinates(new Coordinates(pointOfInterest.getMapPoint().getX(), pointOfInterest.getMapPoint().getY()));
        return pointOfInterestDto;
    }
}
