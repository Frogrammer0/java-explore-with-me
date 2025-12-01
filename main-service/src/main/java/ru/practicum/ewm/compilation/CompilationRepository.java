package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findByPinned(boolean pinned, Pageable pageable);
}
