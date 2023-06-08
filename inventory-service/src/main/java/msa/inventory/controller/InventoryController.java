package msa.inventory.controller;

import lombok.RequiredArgsConstructor;
import msa.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // http://localhost:8091/api/inventory?sku-code=iphone-13&sku-code=iphone13-red
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}
