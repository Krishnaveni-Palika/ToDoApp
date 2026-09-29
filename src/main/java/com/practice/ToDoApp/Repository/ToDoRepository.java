package com.practice.ToDoApp.Repository;

import com.practice.ToDoApp.Entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    Optional<ToDo> findByUserIdAndTaskNumber(Integer userId, Integer taskNumber);
    List<ToDo> findByUserId(Integer userId);
    List<ToDo> findByUserIdIn(List<Integer> userIds);
}

