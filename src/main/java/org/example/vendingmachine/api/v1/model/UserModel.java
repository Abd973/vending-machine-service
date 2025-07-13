package org.example.vendingmachine.api.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.vendingmachine.api.v1.dto.Role;
import jakarta.persistence.*;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @Column(name = "name", nullable = false)
    @Size(min = 3, max = 50)
    private String name;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "deposit")
    @Min(value = 0, message = "Deposit must be greater than or equal 0")
    private int deposit;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductModel> products = new ArrayList<>();

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<TokenModel> tokens = new ArrayList<>();

    public UserModel(UserRequestDto userDto) {
        this.name = userDto.getName();
        this.password = userDto.getPassword();
        this.role = userDto.getRole();
        this.deposit = userDto.getDeposit();
    }
}
