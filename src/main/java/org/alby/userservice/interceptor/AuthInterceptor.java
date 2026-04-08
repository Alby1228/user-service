package org.alby.userservice.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.alby.userservice.context.UserContext;
import org.alby.userservice.entity.enums.ErrCodeEnum;
import org.alby.userservice.util.RespUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String HEADER_USER_ID = "userId";
    private static final String HEADER_USERNAME = "userName";

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader(HEADER_USER_ID);
        String username = request.getHeader(HEADER_USERNAME);

        if (userIdStr == null || userIdStr.isBlank()) {
            writeUnauthorized(response);
            return false;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            UserContext.set(new UserContext.CurrentUser(userId, username));
            return true;
        } catch (NumberFormatException e) {
            log.warn("Invalid userId header: {}", userIdStr);
            writeUnauthorized(response);
            return false;
        }
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
