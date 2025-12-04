package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = """
            select * from categories c
            order by c.id desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Category> findAllWithOffset(@Param("from") int from,
                                     @Param("size") int size);

    Optional<Category> findById(Long id);


    boolean existsByName(String name);
}
