package com.productcard.card.shop.service.user;

import com.productcard.card.shop.model.User;
import com.productcard.card.shop.request.CreateUserRequest;
import com.productcard.card.shop.request.UserUpdateRequest;

public interface IUserService {

    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);
}
