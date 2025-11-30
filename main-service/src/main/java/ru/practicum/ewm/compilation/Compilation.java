package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import ru.practicum.ewm.event.Event;

import java.util.List;

@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String title;

    @Column
    boolean pinned;

    @ManyToMany
    @JoinTable(name = "events")
    List<Event> events;
}
