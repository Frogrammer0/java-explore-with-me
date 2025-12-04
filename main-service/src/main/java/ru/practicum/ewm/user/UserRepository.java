package ru.practicum.ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            select * from users u
            where u.id in :ids
            order by u.id desc
            limit :size offset :from
            """, nativeQuery = true)
    List<User> findByIdIn(@Param("ids") List<Long> ids,
                          @Param("from") int from,
                          @Param("size") int size);

    @Query(value = """
            select * from users u
            order by u.id desc
            limit :size offset :from
            """, nativeQuery = true)
    List<User> findAllWithOffset(@Param("from") int from,
                                 @Param("size") int size);

    boolean existsByEmail(String email);

}
