package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepositoryImpl playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepositoryImpl playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player create(Player player) {
        return playerRepository.save(player);
    }


    @Override
    public Player getById(Long id) {
        return playerRepository.findById(id).get();
    }


    @Override
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return playerRepository.existsById(id);
    }

    @Override
    public Player updatePlayer(Long id, Player newPlayer) {
        //ищем, что нужно обновить
        Player playerToUpdate = getById(id);

    if (newPlayer.getName() != null && newPlayer.getName().length() > 0) {
        playerToUpdate.setName(newPlayer.getName());
    }

    if (newPlayer.getTitle() != null && newPlayer.getTitle().length() > 0) {
        playerToUpdate.setTitle(newPlayer.getTitle());
    }
    if (newPlayer.getProfession() != null && newPlayer.getProfession().name() != null && newPlayer.getProfession().name().length() > 0) {
        playerToUpdate.setProfession(newPlayer.getProfession());
     }

    if (newPlayer.getRace() != null && newPlayer.getRace().name() != null && newPlayer.getRace().name().length() > 0) {
        playerToUpdate.setRace(newPlayer.getRace());
    }
    if (newPlayer.getBirthday() != null) {
        playerToUpdate.setBirthday(newPlayer.getBirthday());
    }
    if (newPlayer.getExperience() != null) {
        playerToUpdate.setExperience(newPlayer.getExperience());
    }
    if (newPlayer.getLevel() != null) {
        playerToUpdate.setLevel(newPlayer.getLevel());
    }
    if (newPlayer.getUntilNextLevel() != null) {
        playerToUpdate.setUntilNextLevel(newPlayer.getUntilNextLevel());
    }
    if (newPlayer.getBanned() != null) {
        playerToUpdate.setBanned(newPlayer.getBanned());
    }
    return playerRepository.save(playerToUpdate);

    }

    public List<Player> getCount(String name, String title, Race race, Profession profession, Long after, Long before,
                                 Boolean banned, Integer minExperience, Integer maxExperience,
                                 Integer minLevel, Integer maxLevel) {

        List<Player> playerPage = playerRepository.findAll(findAccordingTerms(name, title,race,profession,
                millisecondsAfter(after)
                ,millisecondsAfter(before),banned,minExperience,maxExperience,
                minLevel,maxLevel));

        return playerPage;

    }

    @Override
    public Page<Player> getSortedList(String name, String title, Race race, Profession profession, Long after, Long before,
                                      Boolean banned, Integer minExperience, Integer maxExperience,
                                      Integer minLevel, Integer maxLevel, PlayerOrder order, Integer pageNumber, Integer size) {

        Pageable pageable = null;
        if (pageNumber==null){pageNumber=0;}
        if(size==null){size=3;}
        if(order==null){pageable= PageRequest.of(pageNumber,size, Sort.Direction.ASC,PlayerOrder.ID.getFieldName());}
        if(order!=null){pageable= PageRequest.of(pageNumber,size, Sort.Direction.ASC,order.getFieldName());}



        Page<Player> playerPage = playerRepository.findAll(findAccordingTerms(name, title,race,profession,millisecondsAfter(after)
                ,millisecondsAfter(before),banned,minExperience,maxExperience,
                minLevel,maxLevel),pageable);

        return playerPage;
    }

        private Date millisecondsAfter(Long millis) {
        return millis == null ? null : new Date(millis);
    }

    // метод делает набор условий для формирования запроса в базу данных
    public Specification<Player> findAccordingTerms(String name, String title, Race race, Profession profession, Date after, Date before,
                                                    Boolean banned, Integer minExperience, Integer maxExperience,
                                                    Integer minLevel,Integer maxLevel){
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (name != null) {
                    predicates.add(cb.like(root.get("name"), "%" + name + "%"));
                }

                if (title != null) {
                    predicates.add(cb.like(root.get("title"), "%" + title + "%"));
                }

                if (race != null) {
                    predicates.add(cb.equal(root.get("race"), race));
                }

                if (profession != null) {
                    predicates.add(cb.equal(root.get("profession"), profession));
                }

                if (after != null) {
                predicates.add(cb.greaterThan(root.get("birthday"), after));
                }

                if (before != null) {
                predicates.add(cb.lessThan(root.get("birthday"), before));
                }

                if (banned != null) {
                    predicates.add(cb.equal(root.get("banned"), banned));
                }

                if (minExperience != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("experience"), minExperience));
                }

                if (maxExperience != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("experience"), maxExperience));
                }

                if (minLevel != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("level"), minLevel));
                }

                if (maxLevel != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("level"), maxLevel));
                }

                return cb.and(predicates.toArray(new Predicate[]{}));
            }
        };
    }
}


//@Override
//public Player updatePlayer(Long id, Player newPlayer) {
//    Player playerToUpdate = getById(id);//ищем, что нужно обновить
//        // это убираем


//Update
////if (newPlayer.getName() == null || newPlayer.getName().length() == 0) {
////    return playerRepository.save(playerToUpdate);
////}
//    if (newPlayer.getName() != null && newPlayer.getName().length() > 0) {
//        playerToUpdate.setName(newPlayer.getName());
//    }
//    if (newPlayer.getTitle() != null && newPlayer.getTitle().length() > 0) {
//        playerToUpdate.setTitle(newPlayer.getTitle());
//    }
//    if (newPlayer.getProfession() != null && newPlayer.getProfession().name() != null && newPlayer.getProfession().name().length() > 0) {
//        playerToUpdate.setProfession(newPlayer.getProfession());
//    }
//    if (newPlayer.getRace() != null && newPlayer.getRace().name() != null && newPlayer.getRace().name().length() > 0) {
//        playerToUpdate.setRace(newPlayer.getRace());
//    }
//    if (newPlayer.getBirthday() != null) {
//        playerToUpdate.setBirthday(newPlayer.getBirthday());
//    }
//    if (newPlayer.getExperience() != null) {
//        playerToUpdate.setExperience(newPlayer.getExperience());
//    }
//    if (newPlayer.getLevel() != null) {
//        playerToUpdate.setLevel(newPlayer.getLevel());
//    }
//    if (newPlayer.getUntilNextLevel() != null) {
//        playerToUpdate.setUntilNextLevel(newPlayer.getUntilNextLevel());
//    }
//    if (newPlayer.getBanned() != null) {
//        playerToUpdate.setBanned(newPlayer.getBanned());
//    }
//    return playerRepository.save(playerToUpdate);
//}
