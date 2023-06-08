package msa.order.service;

import lombok.RequiredArgsConstructor;
import msa.order.dto.OrderLineItemsDto;
import msa.order.dto.OrderRequest;
import msa.order.model.Order;
import msa.order.model.OrderLineItems;
import msa.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto).toList();

        order.setOrderLineItemsList(orderLineItems);

        // Call Inventory Service, and place order if product is in stock
        Boolean result = webClient.get()
                .uri("http://localhost:8092/api/inventory")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (result) {
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
