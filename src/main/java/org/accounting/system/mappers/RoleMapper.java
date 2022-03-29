package org.accounting.system.mappers;

import org.accounting.system.dtos.authorization.RoleRequestDto;
import org.accounting.system.dtos.authorization.RoleResponseDto;
import org.accounting.system.dtos.authorization.update.UpdateCollectionPermissionDto;
import org.accounting.system.dtos.authorization.update.UpdateRoleRequestDto;
import org.accounting.system.entities.authorization.CollectionPermission;
import org.accounting.system.entities.authorization.Role;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * This interface is responsible for turning a Role Entity into a request/response and vice versa.
 */
@Mapper(uses= UtilMapper.class, imports = {StringUtils.class})
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper( RoleMapper.class );

    List<RoleResponseDto> rolesToResponse(List<Role> roles);

    List<CollectionPermission> updateCollectionPermissionToCollectionPermission(List<UpdateCollectionPermissionDto> permissions);

    Role requestToRole(RoleRequestDto request);

    RoleResponseDto roleToResponse(Role response);

    @Mapping(target = "name", expression = "java(StringUtils.isNotEmpty(request.name) ? request.name : role.getName())")
    @Mapping(target = "description", expression = "java(StringUtils.isNotEmpty(request.description) ? request.description : role.getDescription())")
    @Mapping(source = "collectionPermission", target = "collectionPermission", qualifiedByName = "permissions")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRoleFromDto(UpdateRoleRequestDto request, @MappingTarget Role role);

    @Named("permissions")
    default List<CollectionPermission> permissions(List<UpdateCollectionPermissionDto> permissions) {

        if(Objects.nonNull(permissions) && permissions
                    .stream()
                    .allMatch(collectionPermission -> Objects.nonNull(collectionPermission.collection) && Objects.nonNull(collectionPermission.permissions))
                    &&
                    permissions
                            .stream()
                            .map(cp->cp.permissions)
                            .flatMap(Collection::stream)
                            .allMatch(permission -> Objects.nonNull(permission.operation) && Objects.nonNull(permission.accessType))){

                return updateCollectionPermissionToCollectionPermission(permissions);
            }
        return null;
    }
}