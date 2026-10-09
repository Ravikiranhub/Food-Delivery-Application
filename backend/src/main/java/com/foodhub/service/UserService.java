package com.foodhub.service;

import com.foodhub.dto.user.AddressRequest;
import com.foodhub.dto.user.AddressResponse;
import com.foodhub.dto.user.ProfileUpdateRequest;
import com.foodhub.dto.user.UserResponse;
import com.foodhub.entity.Address;
import com.foodhub.entity.User;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.mapper.UserMapper;
import com.foodhub.repository.AddressRepository;
import com.foodhub.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UserResponse profile() {
        return UserMapper.toResponse(currentUserService.getCurrentUser());
    }

    @Transactional
    public UserResponse updateProfile(ProfileUpdateRequest request) {
        User user = currentUserService.getCurrentUser();
        user.setName(request.name());
        user.setPhone(request.phone());
        return UserMapper.toResponse(userRepository.save(user));
    }

    public List<AddressResponse> addresses() {
        User user = currentUserService.getCurrentUser();
        return addressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(user.getId())
                .stream()
                .map(UserMapper::toAddressResponse)
                .toList();
    }

    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User user = currentUserService.getCurrentUser();
        if (request.defaultAddress()) {
            clearDefaultAddress(user.getId());
        }
        Address address = Address.builder()
                .user(user)
                .label(request.label())
                .line1(request.line1())
                .line2(request.line2())
                .city(request.city())
                .state(request.state())
                .pincode(request.pincode())
                .defaultAddress(request.defaultAddress())
                .build();
        return UserMapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        User user = currentUserService.getCurrentUser();
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (request.defaultAddress()) {
            clearDefaultAddress(user.getId());
        }
        address.setLabel(request.label());
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPincode(request.pincode());
        address.setDefaultAddress(request.defaultAddress());
        return UserMapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long id) {
        User user = currentUserService.getCurrentUser();
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse setDefaultAddress(Long id) {
        User user = currentUserService.getCurrentUser();
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        clearDefaultAddress(user.getId());
        address.setDefaultAddress(true);
        return UserMapper.toAddressResponse(addressRepository.save(address));
    }

    private void clearDefaultAddress(Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(userId);
        addresses.forEach(address -> address.setDefaultAddress(false));
        addressRepository.saveAll(addresses);
    }
}

