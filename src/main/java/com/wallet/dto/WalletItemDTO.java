package com.wallet.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class WalletItemDTO {
    private Long id;
    @NotNull(message = "A data não pode ser nula")
    private Date date;
    @NotNull(message = "O tipo não pode ser nulo")
    @Pattern(regexp = "^(ENTRADA|SAIDA)$", message = "O tipo somente pode ser ENTRADA ou SAIDA")
    private String type;
    @NotNull(message = "A descrição não pode ser nula")
    @Length(min = 5, message = "A descrição deve ter no mínimo 5 caraceteres")
    private String description;
    @NotNull(message = "O valor não pode ser nulo")
    private BigDecimal value;
    @NotNull(message = "O ID da carteira não pode ser nulo")
    private Long wallet_id;
}
