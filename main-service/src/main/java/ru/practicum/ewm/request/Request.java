package ru.practicum.ewm.request;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @Column
    private EventStatus status;

    @Column
    private LocalDateTime created;
}
