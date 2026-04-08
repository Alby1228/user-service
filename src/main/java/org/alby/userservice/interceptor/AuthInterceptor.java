package org.alby.userservice.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.alby.userservice.context.UserContext;
import org.alby.userservice.entity.enums.ErrCodeEnum;
import org.alby.userservice.service.impl.AuthServiceImpl;
import org.alby.userservice.util.JwtUtil;
import org.alby.userservice.util.RedisUtil;
import org.alby.userservice.util.RespUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY_PREFIX = "user:session:";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response);
            return false;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!jwtUtil.isTokenValid(token)) {
            writeUnauthorized(response);
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String sessionId = jwtUtil.getSessionIdFromToken(token);

        if (userId == null || sessionId == null) {
            writeUnauthorized(response);
            return false;
        }

        String sessionData = redisUtil.get(SESSION_KEY_PREFIX + sessionId);
        if (sessionData == null) {
            writeUnauthorized(response);
            return false;
        }

        AuthServiceImpl.SessionData session = objectMapper.readValue(sessionData, AuthServiceImpl.SessionData.class);

        UserContext.set(new UserContext.CurrentUser(session.userId(), session.username()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        RespUtil<Void> resp = RespUtil.error(ErrCodeEnum.UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(resp));
    }
}
