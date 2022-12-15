package com.example.userservice.kafka;

import com.example.userservice.jpa.CountEntity;
import com.example.userservice.jpa.CountRepository;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
    CountRepository repository;
    UserService userService;

    @Autowired
    public KafkaConsumer(CountRepository repository,
                         UserService userService){
        this.userService=userService;
        this.repository=repository;
    }

    @KafkaListener(topics="music-count-topic")
    public void processMessage(String kafkaMessage){
        log.info("Kafka Message: =====> "+kafkaMessage);

        Map<Object, Object> map=new HashMap<>();
        ObjectMapper mapper=new ObjectMapper();
        try{
            map=mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>(){});
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }

        CountEntity entity=repository.findByUserIdAndMusic((String) map.get("userId"), (String)map.get("musicId"));
        if(entity==null){
            userService.createCount((String)map.get("userId"),(String)map.get("musicId"));
            entity=repository.findByUserIdAndMusic(
                    (String) map.get("userId"), (String)map.get("musicId"));
        }

        entity.setCount(entity.getCount()+1);
        repository.save(entity);
    }
}
