package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAdviceVO {
    private BigDecimal totalBudget;
    private BigDecimal estimatedCost;
    private String currency;
    private List<BudgetItemVO> details;
}
