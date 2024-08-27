package com.telegrambot.marketplace.entity.user;

import com.telegrambot.marketplace.entity.inventory.ProductPortion;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.order.Basket;
import com.telegrambot.marketplace.entity.order.Order;
import com.telegrambot.marketplace.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_chat_id", columnList = "chat_id"),
                @Index(name = "idx_users_user_name", columnList = "user_name")
        })
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType permissions;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    @ToString.Exclude
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    @ToString.Exclude
    private City city;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn
    @ToString.Exclude
    private State state;

    @Column(nullable = false)
    private Long discount;

    @Column(name = "user_name", unique = true)
    private String userName;

    @Column
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<User> referrals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id")
    @ToString.Exclude
    private User referrer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Basket basket;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ProductPortion> courierProductPortions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Token> tokens;
}
