package com.mycompany.chatappbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycompany.chatappbackend.model.entity.MessageActivityUserKey;
import com.mycompany.chatappbackend.model.entity.MessageUserActivity;

@Repository
public interface MesageUserActivityRepository extends JpaRepository<MessageUserActivity, MessageActivityUserKey>{

}
