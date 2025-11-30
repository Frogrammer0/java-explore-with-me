package ru.practicum.ewm.event;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String annotation;

    @Column
    String description;

    @Column
    String title;

    @Column
    LocalDateTime eventDate;

    @Column
    LocalDateTime publishedOn;

    @Column
    boolean paid;

    @Column
    int participantLimit;

    @Column
    boolean requestModeration;

    @Column
    EventState state;

    @Column
    int confirmedRequests;

    @Column
    int views;

    @ManyToOne(fetch = FetchType.LAZY)
    User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    Category category;

    @Embedded
    Location location;

}
