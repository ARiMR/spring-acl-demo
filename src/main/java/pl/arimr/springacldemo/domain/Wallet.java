package pl.arimr.springacldemo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
public class Wallet extends BaseEntity {

    private BigDecimal amount;
    private String description;

}
