package com.tpe.repository;

import com.tpe.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.module.ResolutionException;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName) throws ResolutionException;

    Boolean existsByUserName(String userName);

}
