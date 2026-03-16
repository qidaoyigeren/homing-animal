package org.example.hominganimal.domain.shared.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 * 技术亮点：基于Spring Event机制实现领域事件发布/订阅，解耦业务逻辑
 */
public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public LocalDateTime getOccurredOn() { return occurredOn; }
}