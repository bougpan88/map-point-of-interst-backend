//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.19 at 04:42:07 PM EEST 
//


package com.boug.geospatial.dto.generated_soap_dtos;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the map_points package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: map_points
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetNearestPointRequest }
     * 
     */
    public GetNearestPointRequest createGetNearestPointRequest() {
        return new GetNearestPointRequest();
    }

    /**
     * Create an instance of {@link Coordinates }
     * 
     */
    public Coordinates createCoordinates() {
        return new Coordinates();
    }

    /**
     * Create an instance of {@link GetNearestPointResponse }
     * 
     */
    public GetNearestPointResponse createGetNearestPointResponse() {
        return new GetNearestPointResponse();
    }

    /**
     * Create an instance of {@link PointOfInterestDto }
     * 
     */
    public PointOfInterestDto createPointOfInterestDto() {
        return new PointOfInterestDto();
    }

    /**
     * Create an instance of {@link GetNearestCityNameRequest }
     * 
     */
    public GetNearestCityNameRequest createGetNearestCityNameRequest() {
        return new GetNearestCityNameRequest();
    }

    /**
     * Create an instance of {@link GetNearestCityNameResponse }
     * 
     */
    public GetNearestCityNameResponse createGetNearestCityNameResponse() {
        return new GetNearestCityNameResponse();
    }

    /**
     * Create an instance of {@link GetPointsWithGreaterCounterRequest }
     * 
     */
    public GetPointsWithGreaterCounterRequest createGetPointsWithGreaterCounterRequest() {
        return new GetPointsWithGreaterCounterRequest();
    }

    /**
     * Create an instance of {@link GetPointsWithGreaterCounterResponse }
     * 
     */
    public GetPointsWithGreaterCounterResponse createGetPointsWithGreaterCounterResponse() {
        return new GetPointsWithGreaterCounterResponse();
    }

}
