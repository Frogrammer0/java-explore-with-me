package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.Event;

import java.util.List;

@Entity
@Table(name = "compilations")
@Builder
@Getter
@Setter
@ToString
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private Boolean pinned;

    @ManyToMany
    @JoinTable(name = "events")
    private List<Event> events;
}
