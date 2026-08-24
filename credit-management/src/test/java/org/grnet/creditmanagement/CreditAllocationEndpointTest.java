//package org.grnet.creditmanagement;
//
//import io.quarkus.test.InjectMock;
//import io.quarkus.test.common.http.TestHTTPEndpoint;
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.http.ContentType;
//import jakarta.inject.Inject;
//import org.grnet.creditmanagement.dtos.CreditAllocationRequestDto;
//import org.grnet.creditmanagement.dtos.CreditAllocationResponseDto;
//import org.grnet.creditmanagement.entities.CreditAllocationEntity;
//import org.grnet.creditmanagement.exceptions.RatingPolicyConflictExceptionMapper;
//import org.grnet.creditmanagement.repositories.CreditAllocationRepository;
//import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.mockito.Mockito;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//
//import static io.restassured.RestAssured.given;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyString;
//
//@QuarkusTest
//@TestHTTPEndpoint(CreditAllocationEndpoint.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class CreditAllocationEndpointTest {
//
//    @InjectMock
//    ExternalEntityLookupRepository externalEntityLookupRepository;
//
//    @Inject
//    CreditAllocationRepository creditAllocationRepository;
//
//    private static final String PROJECT_ID = "707f1f77bcf86cd799439011";
//    private static final String GROUP_ID = "group-42";
//
//    @BeforeEach
//    void before() {
//
//        creditAllocationRepository.deleteAll();
//
//        Mockito.when(externalEntityLookupRepository.projectExists(anyString())).thenReturn(true);
//    }
//
//    private CreditAllocationRequestDto request(Instant validFrom, Instant validTo, Double totalCredits) {
//
//        var request = new CreditAllocationRequestDto();
//        request.validFrom = validFrom;
//        request.validTo = validTo;
//        request.totalCredits = totalCredits;
//
//        return request;
//    }
//
//    @Test
//    public void createAllocationSuccess() {
//
//        var validFrom = Instant.parse("2026-08-01T00:00:00Z");
//        var validTo = Instant.parse("2026-09-01T00:00:00Z");
//
//        var response = given()
//                .contentType(ContentType.JSON)
//                .body(request(validFrom, validTo, 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .as(CreditAllocationResponseDto.class);
//
//        assertNotNull(response.id);
//        assertEquals(PROJECT_ID, response.projectId);
//        assertEquals(GROUP_ID, response.groupId);
//        assertEquals(1000D, response.totalCredits);
//        assertEquals(validFrom, response.validFrom);
//        assertEquals(validTo, response.validTo);
//
//        assertEquals(1, creditAllocationRepository.findByProjectAndGroup(PROJECT_ID, GROUP_ID).size());
//    }
//
//    @Test
//    public void createAllocationRequestBodyIsEmpty() {
//
//        given()
//                .contentType(ContentType.JSON)
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(400);
//    }
//
//    @Test
//    public void createAllocationCannotConsumeContentType() {
//
//        given()
//                .body(request(Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(415);
//    }
//
//    @Test
//    public void createAllocationValidFromIsNull() {
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request(null, Instant.now(), 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(400);
//    }
//
//    @Test
//    public void createAllocationValidToIsNull() {
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request(Instant.now(), null, 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(400);
//    }
//
//    @Test
//    public void createAllocationTotalCreditsIsNull() {
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request(Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), null))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(400);
//    }
//
//    @Test
//    public void createAllocationTotalCreditsIsNotPositive() {
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request(Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), -100D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(400);
//    }
//
//    @Test
//    public void createAllocationValidFromNotBeforeValidTo() {
//
//        var same = Instant.parse("2026-08-01T00:00:00Z");
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request(same, same, 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(400);
//    }
//
//    @Test
//    public void createAllocationProjectNotFound() {
//
//        Mockito.when(externalEntityLookupRepository.projectExists(anyString())).thenReturn(false);
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request(Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(404);
//    }
//
//    @Test
//    public void createAllocationOverlappingPeriodConflicts() {
//
//        var existing = new CreditAllocationEntity();
//        existing.setId("existing-allocation");
//        existing.setProjectId(PROJECT_ID);
//        existing.setGroupId(GROUP_ID);
//        existing.setTotalCredits(500D);
//        existing.setValidFrom(Instant.parse("2026-08-01T00:00:00Z"));
//        existing.setValidTo(Instant.parse("2026-09-01T00:00:00Z"));
//
//        creditAllocationRepository.persist(existing);
//
//        var overlappingFrom = Instant.parse("2026-08-15T00:00:00Z");
//        var overlappingTo = Instant.parse("2026-10-01T00:00:00Z");
//
//        var response = given()
//                .contentType(ContentType.JSON)
//                .body(request(overlappingFrom, overlappingTo, 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(400)
//                .extract()
//                .as(RatingPolicyConflictExceptionMapper.ErrorResponse.class);
//
//        assertEquals(400, response.code);
//        assertTrue(response.message.contains("overlaps with an existing Credit Allocation"));
//
//        assertEquals(1, creditAllocationRepository.findByProjectAndGroup(PROJECT_ID, GROUP_ID).size());
//    }
//
//    @Test
//    public void createAllocationNonOverlappingPeriodSucceeds() {
//
//        var existing = new CreditAllocationEntity();
//        existing.setId("existing-allocation");
//        existing.setProjectId(PROJECT_ID);
//        existing.setGroupId(GROUP_ID);
//        existing.setTotalCredits(500D);
//        existing.setValidFrom(Instant.parse("2026-08-01T00:00:00Z"));
//        existing.setValidTo(Instant.parse("2026-09-01T00:00:00Z"));
//
//        creditAllocationRepository.persist(existing);
//
//        var nextFrom = Instant.parse("2026-09-01T00:00:00Z");
//        var nextTo = Instant.parse("2026-10-01T00:00:00Z");
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(request(nextFrom, nextTo, 1000D))
//                .post("/{project_id}/groups/{group_id}/allocations", PROJECT_ID, GROUP_ID)
//                .then()
//                .assertThat()
//                .statusCode(200);
//
//        assertEquals(2, creditAllocationRepository.findByProjectAndGroup(PROJECT_ID, GROUP_ID).size());
//    }
//}