package org.example.vendingmachine.api.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductBuyResponseDto {
    private int totalCost;
    private SoldProductInfoDto soldProductInfoDto;
    private List<Integer> changes;

    @Override
    public String toString() {
        return "ProductBuyResponseDto: {totalCost: %s, soldProductInfoDto: %s, changes: %s}".formatted(totalCost, soldProductInfoDto, changes);
    }
}
