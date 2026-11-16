package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import main.model.visitor.UserVisitor;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Reporter.class, name = "REPORTER"),
        @JsonSubTypes.Type(value = Developer.class, name = "DEVELOPER"),
        @JsonSubTypes.Type(value = Manager.class, name = "MANAGER")
})
public abstract class User {
    private String username;
    private String email;
    private String role;

    @JsonIgnore
    private final List<String> notifications = new ArrayList<>();

    public User() {
    }

    public abstract <R> R accept(UserVisitor<R> visitor);

    /**
     * Adauga o notificare.
     * @param message mesajul.
     */
    public final void addNotification(final String message) {
        this.notifications.add(message);
    }

    public final List<String> getNotifications() {
        return notifications;
    }

    public final String getUsername() {
        return username;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(final String email) {
        this.email = email;
    }

    public final String getRole() {
        return role;
    }

    public final void setRole(final String role) {
        this.role = role;
    }
}
