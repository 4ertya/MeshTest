package com.banking.mapper;

import com.banking.dto.response.UserResponse;
import com.banking.entity.EmailData;
import com.banking.entity.PhoneData;
import com.banking.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "emails", source = "emails", qualifiedByName = "emailsToStrings")
    @Mapping(target = "phones", source = "phones", qualifiedByName = "phonesToStrings")
    @Mapping(target = "balance", source = "account.balance")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Named("emailsToStrings")
    default List<String> emailsToStrings(List<EmailData> emails) {
        if (emails == null) return List.of();
        return emails.stream().map(EmailData::getEmail).collect(Collectors.toList());
    }

    @Named("phonesToStrings")
    default List<String> phonesToStrings(List<PhoneData> phones) {
        if (phones == null) return List.of();
        return phones.stream().map(PhoneData::getPhone).collect(Collectors.toList());
    }
}
