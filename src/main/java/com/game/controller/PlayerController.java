package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@Validated
@RequestMapping("/rest/players")
public class PlayerController {

    @Autowired
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    @PostMapping(value = "")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if (player.getName() == null || player.getName().length() == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        long year1970milliseconds = 62167195440000L;
        long year2000milliseconds = 63113904000000L;
        long year3000milliseconds = 94670856000000L;
        long oneDayMilliseconds = 86400000L;

        Date yearLowerBoundMillis = new Date(year2000milliseconds - year1970milliseconds);
        Date yearUpperBoundMillis = new Date(year3000milliseconds - year1970milliseconds + oneDayMilliseconds);

        if (!(player.getBirthday().after(yearLowerBoundMillis) &&
                player.getBirthday().before(yearUpperBoundMillis))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //experience
        if (!(player.getExperience() > 0 && player.getExperience() < 10000000L)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //титул персонажа не более 30
        if (player.getTitle().length() > 30) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        Player player1 = playerService.create(player);

        int level = ((int) Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();

        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevel);

        return new ResponseEntity<>(player1, HttpStatus.OK);
    }

    //produces = "application/json" не играет роли
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Player> getPlayer(@PathVariable(name = "id") Long id) {
        if (id == null || id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!playerService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Player player = playerService.getById(id);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @GetMapping(value = "")
    public ResponseEntity<List<Player>> getPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false) PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize

    ) {
        final List<Player> playerList = playerService.getSortedList(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize).getContent();


        return new ResponseEntity<>(playerList, HttpStatus.OK);
    }

    @GetMapping(value = "/count")
    public ResponseEntity<Integer> getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel

    ) {
        final Integer count = playerService.getCount(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel).size();


        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable(name = "id") Long id) {
        if (id == null || id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!playerService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        playerService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable(name = "id") Long id, @RequestBody Player player) {
        if (id == null || id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!playerService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        long year1970milliseconds = 62167195440000L;
        long year2000milliseconds = 63113904000000L;
        long year3000milliseconds = 94670856000000L;
        long oneDayMilliseconds = 86400000L;

        Date yearLowerBoundMillis = new Date(year2000milliseconds - year1970milliseconds);
        Date yearUpperBoundMillis = new Date(year3000milliseconds - year1970milliseconds + oneDayMilliseconds);


        if (player.getBirthday() != null) {
            if (!(player.getBirthday().after(yearLowerBoundMillis) &&
                    player.getBirthday().before(yearUpperBoundMillis))) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        //experience
        if (player.getExperience() != null) {
            if (!(player.getExperience() > 0 && player.getExperience() < 10000000L)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        //титул персонажа не более 30

        if (player.getTitle() != null && player.getTitle().length() > 30) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (player.getExperience() != null) {
            int level = ((int) Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
            int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
            player.setLevel(level);
            player.setUntilNextLevel(untilNextLevel);
        }

        Player player1 = playerService.updatePlayer(id, player);

        return new ResponseEntity<>(player1, HttpStatus.OK);
    }
}

