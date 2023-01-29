package org.example.graphql.controller;

import org.example.graphql.model.Author;
import org.example.graphql.model.AuthorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface AuthorMapper {

    @Mapping(target="tin", source="tin", qualifiedByName="encryptTin")
    @Mapping(target="tinHash", source="tin", qualifiedByName="hashTin")
    Author dtoToEntity(AuthorDto dto);

    @Mapping(target="tin", source="tin", qualifiedByName="decryptTin")
    AuthorDto entityToDto(Author entity);

    @Named("encryptTin")
    default String encryptTin(String tin) {
        return Util.encrypt(tin);
    }
    @Named("decryptTin")
    default String decryptTin(String tin) {
        return Util.decrypt(tin);
    }

    @Named("hashTin")
    default String hashTin(String tin) {
        return Util.hash(tin);
    }
}
