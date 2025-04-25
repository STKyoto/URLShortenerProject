package com.example.demo.repository;

import com.example.demo.model.Link;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository  extends JpaRepository<Link, Integer> {
    Optional<Link> findByShortUrl(String url);
    boolean existsByShortUrl(String url);

    @Modifying
    @Query("UPDATE Link l SET l.clickCount = l.clickCount + 1 WHERE l.id = :id")
    void incrementClickCount(@Param("id") int id);

    List<Link> findByUser(User user);
    @Query("SELECT l FROM Link l WHERE l.user = :user AND (l.expiresAt IS NULL OR l.expiresAt > :now)")
    List<Link> findActiveLinksByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    Optional<Link> findByShortUrlAndUser(String shortUrl, User user);
}