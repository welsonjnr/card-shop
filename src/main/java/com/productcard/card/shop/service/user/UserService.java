package com.productcard.card.shop.service.user;

import com.productcard.card.shop.exceptions.AlreadyExistsException;
import com.productcard.card.shop.exceptions.ResourceNotFoundException;
import com.productcard.card.shop.model.User;
import com.productcard.card.shop.repository.UserRepository;
import com.productcard.card.shop.request.CreateUserRequest;
import com.productcard.card.shop.request.UserUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request).filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(request.getPassword());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    return userRepository.save(user);
                }).orElseThrow(() -> new AlreadyExistsException("Oops! " + request.getEmail() + " alreary exists!"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository :: delete, () -> {
            throw new ResourceNotFoundException("User not found!");
        });
    }
}
