package notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * order-service OrderPlacedEvent와 같은 클래스인데 이유는 ?
 * 클래스를 공유하는건 추천하지 않는다. 독립적으로 유지해야함
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private String orderNumber;
}

