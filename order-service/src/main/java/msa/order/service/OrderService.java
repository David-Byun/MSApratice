package msa.order.service;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import msa.order.dto.InventoryResponse;
import msa.order.dto.OrderLineItemsDto;
import msa.order.dto.OrderRequest;
import msa.order.model.Order;
import msa.order.model.OrderLineItems;
import msa.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@TimeLimiter(name = "inventory")
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto).toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        // Call Inventory Service, and place order if product is in stock http://localhost:8091/actuator/health에서 확인할 수 있음(서킷브레이커)
        InventoryResponse[] inventoryResponseArray = webClient.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                //retrieve() 메서드는 요청에 응답 받았을 때 그 값을 추출하는 방법
                .bodyToMono(InventoryResponse[].class)
                //return 타입을 설정해서 문자열 객체로 받아오게 되어 있음
                .block();
                //block 논블로킹으로 작동하는 WebClient 를 블로킹 구조로 바꾸기 위해 사용

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);


        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, plz try again!");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
