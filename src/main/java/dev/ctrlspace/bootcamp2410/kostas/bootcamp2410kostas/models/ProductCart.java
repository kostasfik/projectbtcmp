package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class ProductCart {

    private Product product;
    private Double cartQuantity;
}
