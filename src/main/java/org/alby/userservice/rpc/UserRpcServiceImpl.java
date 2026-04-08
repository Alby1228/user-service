package org.alby.userservice.rpc;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alby.albyconfigstater.AlbyRpcProvider;
import org.alby.userservice.dto.response.UserResponse;
import org.alby.userservice.entity.User;
import org.alby.userservice.enums.UserStatus;
import org.alby.userservice.repository.UserRepository;

import java.util.List;

@Slf4j
@AlbyRpcProvider(uri = "/rpc/user")
public class UserRpcServiceImpl implements UserRpcService {

    @Resource
    private UserRepository userRepository;

    @Override
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElse(null);
    }

    @Override
    public List<UserResponse> findByIds(List<Long> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        return users.stream().map(UserResponse::fromEntity).toList();
    }

    @Override
    public UserResponse findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponse::fromEntity)
                .orElse(null);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean isUserActive(Long id) {
        return userRepository.findById(id)
                .map(user -> user.getStatus().equals(UserStatus.ACTIVE.getCode()))
                .orElse(false);
    }
}
