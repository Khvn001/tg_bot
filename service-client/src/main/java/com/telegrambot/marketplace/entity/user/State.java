package com.telegrambot.marketplace.entity.user;

import com.telegrambot.marketplace.enums.StateType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "states")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class State {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "state_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StateType stateType;

    @Column
    private String value;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn
    @ToString.Exclude
    private User user;

    public boolean inState() {
        return stateType != null;
    }
}
