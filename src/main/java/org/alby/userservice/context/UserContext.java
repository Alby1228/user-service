package org.alby.userservice.context;

public class UserContext {

    private static final ThreadLocal<CurrentUser> CURRENT_USER = new ThreadLocal<>();

    public static void set(CurrentUser user) {
        CURRENT_USER.set(user);
    }

    public static CurrentUser get() {
        return CURRENT_USER.get();
    }

    public static Long getUserId() {
        CurrentUser user = CURRENT_USER.get();
        return user != null ? user.userId() : null;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    public record CurrentUser(Long userId, String username) {}
}
