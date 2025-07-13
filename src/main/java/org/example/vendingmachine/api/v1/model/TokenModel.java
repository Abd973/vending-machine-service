package org.example.vendingmachine.api.v1.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "jwt_tokens")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "is_valid",  nullable = false)
    boolean isValid;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "expire_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireAt;

    @ManyToOne
    @JoinColumn(name = "user_id",  nullable = false)
    private UserModel user;

    public TokenModel(String token, Date createdAt, Date expireAt, UserModel user,  boolean isValid) {
        this.token = token;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.user = user;
        this.isValid = isValid;
    }

}
