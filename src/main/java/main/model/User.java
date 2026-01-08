package main.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
}