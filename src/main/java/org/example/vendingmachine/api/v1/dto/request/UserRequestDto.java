package org.example.vendingmachine.api.v1.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vendingmachine.api.v1.dto.Role;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {
    @JsonProperty("user_name")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Password cannot be blank")
    @NotNull
    private String password;

    @NotBlank(message = "Role cannot be blank")
    private Role role;

    private int deposit;

    // for creating BUYER user
    public UserRequestDto(String name, String password, Role role, int deposit) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.deposit = deposit;
    }

    // for creating SELLER user
    public UserRequestDto(String name, String password, Role role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User:{name: %s, role: %s, deposit: %d}"
                .formatted(name,  role, deposit);
    }
}
