package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;


import java.util.List;

public interface PlayerService {

    //Создает нового игрока
    Player create(Player player);

    //Получает игрока по id
    Player getById(Long id);

    //Удаляет игрока
    void delete(Long id);

    boolean existsById(Long id);

    Player updatePlayer(Long id, Player newPlayer);

    List<Player> getCount(String name, String title, Race race, Profession profession, Long after, Long before,
                          Boolean banned, Integer minExperience, Integer maxExperience,
                          Integer minLevel, Integer maxLevel);

    Page<Player> getSortedList(String name, String title, Race race, Profession profession, Long after, Long before,
                               Boolean banned, Integer minExperience, Integer maxExperience,
                               Integer minLevel, Integer maxLevel, PlayerOrder order, Integer pageNumber, Integer size);


}
