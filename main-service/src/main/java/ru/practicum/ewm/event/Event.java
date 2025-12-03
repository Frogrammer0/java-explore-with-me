package ru.practicum.ewm.event;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String annotation;

    @Column
    private String description;

    @Column
    private String title;

    @Column
    private LocalDateTime eventDate;

    @Column
    private LocalDateTime publishedOn;

    @Column
    private LocalDateTime createdOn;

    @Column
    private Boolean paid;

    @Column
    private int participantLimit;

    @Column
    private Boolean requestModeration;

    @Column
    private EventState state;

    @Column
    private int confirmedRequests;

    @Column
    private int views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Embedded
    private Location location;

}
