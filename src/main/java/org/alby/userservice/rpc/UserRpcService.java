package org.alby.userservice.rpc;

import org.alby.userservice.dto.response.UserResponse;

import java.util.List;

/**
 * 用户 RPC 服务接口 - 供其他微服务通过 @AlbyRpcConsumer 调用
 *
 * Consumer 端使用示例:
 * <pre>
 * {@code @AlbyRpcConsumer(url = "${user.service.rpc.url}")}
 * private UserRpcService userRpcService;
 *
 * // application.properties:
 * // user.service.rpc.url=http://user-service:8081/rpc/user
 * </pre>
 */
public interface UserRpcService {

    UserResponse findById(Long id);

    List<UserResponse> findByIds(List<Long> ids);

    UserResponse findByUsername(String username);

    boolean existsById(Long id);

    boolean isUserActive(Long id);
}
