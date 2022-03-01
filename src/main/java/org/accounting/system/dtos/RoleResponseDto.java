package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.entities.authorization.CollectionPermission;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="RoleResponse", description="An object represents the stored Roles.")
public class RoleResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique ID of the role.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The role name.",
            example = "metric_definition_inspector"
    )
    @JsonProperty("name")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The role description explains the operations a user or a service is expected to perform.",
            example = "This role is responsible for creating ..."
    )
    @JsonProperty("description")
    public String description;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = CollectionPermission.class,
            description = "This list encapsulates the permissions upon the API collections. It should have at least one list of permission.",
            minItems = 1
    )
    @JsonProperty("collection_permission")
    public List<CollectionPermission> collectionPermission;
}