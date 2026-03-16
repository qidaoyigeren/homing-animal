package org.example.hominganimal.application.event;

import lombok.RequiredArgsConstructor;
import org.example.hominganimal.domain.shared.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
/**
 * 领域事件发布器 — 桥接Spring Event机制
 */
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {
    private final ApplicationEventPublisher publisher;
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
