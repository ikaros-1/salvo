package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUserName(String userName);
    Boolean existsByUserName(String userName);
}
