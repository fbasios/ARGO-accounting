package org.accounting;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.endpoints.MetricEndpoint;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestHTTPEndpoint(MetricEndpoint.class)
public class MetricEndpointTest {


    @Test
    public void create_metric_bad_request() {

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("metric_registration_id", "507f1f77bcf86cd799439011");

        InformativeResponse response = given()
                .pathParams(pathParams)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);

    }

    @Test
    public void create_metric_no_metric_registration() {

        MetricRequestDto request= new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:13:07Z";

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("metric_registration_id", "507f1f77bcf86cd799439011");

        InformativeResponse response = given()
                .pathParams(pathParams)
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Registration with the following id: 507f1f77bcf86cd799439011", response.message);

    }

    @Test
    public void create_metric_cannot_consume_content_type() {

        MetricRequestDto request= new MetricRequestDto();

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("metric_registration_id", "507f1f77bcf86cd799439011");

        InformativeResponse response = given()
                .pathParams(pathParams)
                .body(request)
                .post()
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);

    }


    @Test
    public void create_metric_internal_server_error() {

        MetricRequestDto request= new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:13:07Z";


        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("metric_registration_id", "iiejijirj33i3i");


         given()
                .pathParams(pathParams)
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(500);

    }

    @Test
    public void create_metric() {

        //first create a metric registration

        MetricRegistrationDtoRequest requestForMetricRegistration= new MetricRegistrationDtoRequest();

        requestForMetricRegistration.metricName = "metric";
        requestForMetricRegistration.metricDescription = "description";
        requestForMetricRegistration.unitType = "SECOND";
        requestForMetricRegistration.metricType = "Aggregated";

        MetricRegistrationDtoResponse metricRegistrationResponse = given()
                .basePath("/accounting-system/metric-registration")
                .body(requestForMetricRegistration)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricRegistrationDtoResponse.class);

        //then execute a request for creating a virtual access metric

        MetricRequestDto requestForMetric= new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:13:07Z";
        requestForMetric.value = 10.8;

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("metric_registration_id", metricRegistrationResponse.id);

        given()
                .pathParams(pathParams)
                .body(requestForMetric)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201);
    }


}