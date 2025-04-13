package com.example.demo.repository;


import com.example.demo.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository  extends JpaRepository<Link, Integer> {
    Optional<Link> findByShortUrl(String url);
    boolean existsByShortUrl(String url);

    //we need to add the field "click count" to the DB
    void incrementClickCount(int id);
}
