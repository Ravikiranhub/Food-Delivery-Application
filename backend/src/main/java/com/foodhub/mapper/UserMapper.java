package com.foodhub.mapper;

import com.foodhub.dto.user.AddressResponse;
import com.foodhub.dto.user.UserResponse;
import com.foodhub.entity.Address;
import com.foodhub.entity.User;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }

    public static AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getState(),
                address.getPincode(),
                address.isDefaultAddress()
        );
    }
}

