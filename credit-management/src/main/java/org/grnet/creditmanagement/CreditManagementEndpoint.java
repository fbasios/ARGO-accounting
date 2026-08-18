package org.grnet.creditmanagement;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/credit-management")
public class CreditManagementEndpoint {

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Hello From CreditManagement.",
            description = "Hello From CreditManagement.")
    @APIResponse(
            responseCode = "200",
            description = "Hello From CreditManagement.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = Object.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = Object.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response helloFromCreditManagement(){

        return Response.ok().build();
    }
}
