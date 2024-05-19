package com.example.demo.security.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByUsername(String username);
    //znajdowanie uzytkownika po imieniu przed szukaniem hasła
    //optional bo czasem user isnieje, a czasem nie i optional sluży do tego by nie
    //wypluwało nulla
}
