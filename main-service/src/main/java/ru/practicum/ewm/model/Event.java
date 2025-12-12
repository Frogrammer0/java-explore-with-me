package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000, columnDefinition = "TEXT")
    private String annotation;

    @Column(length = 7000, columnDefinition = "TEXT")
    private String description;

    @Column(length = 120)
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
    @Enumerated(EnumType.STRING)
    private EventState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Embedded
    private Location location;

}
