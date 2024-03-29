package com.example.harjoitus_18.network

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

@Root(name = "FeatureCollection", strict = false)
data class FeatureCollection(
    @field:ElementList(inline = true, entry = "member")
    var members: List<Member>? = null
)

data class Member(
    @field:ElementList(inline = true, entry = "PointTimeSeriesObservation")
    var measurements: List<PointTimeSeriesObservation>? = null
)

data class PointTimeSeriesObservation(
    @field:Element(name = "phenomenonTime", required = false)
    var phenomenonTime: PhenomenonTime? = null,

    @field:Element(name = "resultTime", required = false)
    var resultTime: ResultTime? = null,

    @field:Element(name = "procedure", required = false)
    var procedure: Procedure? = null,

    @field:Element(name = "parameter", required = false)
    var parameter: Parameter? = null,

    @field:Element(name = "observedProperty", required = false)
    var observedProperty: ObservedProperty? = null,

    @field:Element(name = "featureOfInterest", required = false)
    var featureOfInterest: FeatureOfInterest? = null,

    @field:Attribute(name = "id")
    var id: String? = null,

    @field:ElementList(inline = true, entry = "result")
    var results: List<Result>? = null
)

data class Result(
    @field:Element(name = "MeasurementTimeseries")
    var measurementTimeSeries: MeasurementTimeSeries? = null
)

data class MeasurementTimeSeries(
    @field:Attribute(name = "id")
    var id: String? = null,

    @field:ElementList(inline = true, entry = "point")
    var points: List<Point>? = null
)

data class Point(

    @field:Attribute(name = "srsName", required = false)
    var srsName: String? = null,

    @field:Attribute(name = "srsDimension", required = false)
    var srsDimension: Int? = null,

    @field:ElementList(entry = "MeasurementTVP", inline = true)
    var measurementTVP: List<MeasurementTVP>? = null
)

data class MeasurementTVP(
    @field:Element(name = "time")
    var time: String? = null,

    @field:Element(name = "value")
    var value: Double? = null
)

data class PhenomenonTime(
    @field:Attribute(name = "href", required = false)
    var id: String? = null,

    @field:Element(name = "TimePeriod", required = false)
    var timePeriod: TimePeriod? = null
)

data class TimePeriod(
    @field:Attribute(name = "id")
    var id: String? = null,

    @field:ElementList(entry = "beginPosition", inline = true)
    var beginPosition: List<String>? = null,

    @field:ElementList(entry = "endPosition", inline = true)
    var endPosition: List<String>? = null
)

data class ResultTime(
    @field:Attribute(name = "href", required = false)
    var id: String? = null,

    @field:Element(name = "TimeInstant", required = false)
    var timeInstant: TimeInstant? = null
)

data class Procedure(
    @field:Attribute(name = "href", required = false)
    var id: String? = null,
)

data class TimeInstant(
    @field:Attribute(name = "id")
    var id: String? = null,

    @field:ElementList(entry = "timePosition", inline = true)
    var timePosition: List<String>? = null
)

data class Parameter(
    @field:ElementList(entry = "NamedValue", inline = true)
    var namedValue: List<NamedValue>? = null,
)

data class NamedValue(
    @field:ElementList(entry = "name", inline = true)
    var name: List<String>? = null,

    @field:ElementList(entry = "value", inline = true)
    var value: List<String>? = null
)

data class ObservedProperty(
    @field:Attribute(name = "href", required = false)
    var id: String? = null,
)

data class FeatureOfInterest(
    @field:Element(name = "SF_SpatialSamplingFeature")
    var spatialSamplingFeature: SpatialSamplingFeature? = null
)

data class SpatialSamplingFeature(
    @field:Attribute(name = "id")
    var id: String? = null,

    @field:Element(name = "sampledFeature")
    var sampledFeature: SampledFeature? = null,

    @field:Element(name = "shape")
    var shape: Shape? = null
)

data class SampledFeature(
    @field:Element(name = "LocationCollection")
    var locationCollection: LocationCollection? = null
)

data class LocationCollection(
    @field:Attribute(name = "id")
    var id: String? = null,

    @field:Element(name = "member")
    var member: Member2? = null
)

data class Member2(
    @field:Element(name = "Location")
    var location: Location? = null
)

data class Location(
    @field:Attribute(name = "id")
    var id: String? = null,

    @field:Element(name = "identifier")
    var identifier: Identifier? = null,

    @field:ElementList(name = "name", inline = true)
    var name: List<Name>? = null,

    @field:Element(name = "representativePoint")
    var representativePoint: RepresentativePoint? = null,

    @field:Element(name = "region")
    var region: Region? = null
)

data class Identifier(
    @field:Attribute(name = "codeSpace")
    var id: String? = null
)

data class Name(
    @field:Attribute(name = "codeSpace")
    var id: String? = null
)

data class Region(
    @field:Attribute(name = "codeSpace")
    var codeSpace: String? = null
)

data class Shape(
    @field:Element(name = "Point")
    var point: Point2? = null
)

data class Point2(
    @field:Attribute(name = "id")
    var id: String? = null,

    @field:Attribute(name = "srsName", required = false)
    var srsName: String? = null,

    @field:Attribute(name = "srsDimension" , required = false)
    var srsDimension: Int? = null,

    @field:Element(name = "name")
    var name: String? = null,

    @field:Element(name = "pos")
    var pos: String? = null
)

data class RepresentativePoint(
    @field:Attribute(name = "href", required = false)
    var id: String? = null,
)

interface WeatherDataApiService {

    @GET("wfs/fin")
    suspend fun fetchWeatherData(
        @Query("service") service: String = "WFS",
        @Query("version") version: String = "2.0.0",
        @Query("request") request: String = "GetFeature",
        @Query("storedquery_id") storedqueryId: String = "fmi::observations::weather::timevaluepair",
        @Query("place") place: String
    ): FeatureCollection

    companion object {
        private val BASE_URL = "https://opendata.fmi.fi/"

        fun create(): WeatherDataApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
            return retrofit.create(WeatherDataApiService::class.java)
        }
    }
}