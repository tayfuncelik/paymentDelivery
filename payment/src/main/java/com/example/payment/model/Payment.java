package com.example.payment.model;

import com.example.payment.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PAYMENTS")
public class Payment {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonProperty("payment_id")
    private String paymentId;

    @Column(nullable = false)
    @JsonProperty("payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    @JsonProperty("credit_card")
    private String creditCard;

    @Column(nullable = false)
    @JsonProperty("amount")
    private BigDecimal amount;

    @Column
    @JsonIgnore
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonProperty("account_id")
    private Account account;

    @JsonGetter("account_id")
    public Integer getAccountId() {
        return account.getAccountId();
    }

    @JsonGetter("payment_type")
    public String getPaymentType() {
        return paymentType.getName().toLowerCase();
    }

}
