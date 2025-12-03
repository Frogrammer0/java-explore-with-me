package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import ru.practicum.ewm.event.Event;

import java.util.List;

@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private boolean pinned;

    @ManyToMany
    @JoinTable(name = "events")
    private List<Event> events;
}
