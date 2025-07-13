package org.example.vendingmachine.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vendingmachine.api.v1.dto.Role;
import org.example.vendingmachine.api.v1.model.UserModel;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserResponseDto {
    @JsonProperty("user_id")
    private int id;
    @JsonProperty("user_name")
    private String name;
    private Role role;
    private int deposit;

    public UserResponseDto(UserModel user) {
        this.id = user.getId();
        this.name = user.getName();
        this.role = user.getRole();
        this.deposit = user.getDeposit();
    }

    @Override
    public String toString() {
        return "User:{id: %s,name: %s, role: %s, deposit: %d}"
                .formatted(id, name,  role, deposit);
    }
}
