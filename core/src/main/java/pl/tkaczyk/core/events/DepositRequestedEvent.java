package pl.tkaczyk.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestedEvent {

    private String senderId;
    private String recepientId;
    private BigDecimal amount;
}