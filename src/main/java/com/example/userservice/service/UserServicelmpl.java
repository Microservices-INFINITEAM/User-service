package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.CountDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.CountEntity;
import com.example.userservice.jpa.CountRepository;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service//spring boot에 빈으로 등록-빈 타입이 서비스
@Slf4j //로그출력
@RequiredArgsConstructor
public class UserServicelmpl implements UserService {

    private final UserRepository userRepository;
    private final CountRepository countRepository;

    @Autowired
    CircuitBreakerFactory circuitBreakerFactory;

    Environment env;
//    RestTemplate restTemplate;
    OrderServiceClient orderServiceClient;

    @Autowired
    public UserServicelmpl(UserRepository userRepository, Environment env,
                           //RestTemplate restTemplate,
                           OrderServiceClient orderServiceClient,
                           CountRepository countRepository){
        this.userRepository=userRepository;
        this.env=env;
//        this.restTemplate=restTemplate;
        this.orderServiceClient=orderServiceClient;
        this.countRepository=countRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        //set이 없는데 userDto에 @Data하면 알아서 setter getter 만들어줌

        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity=mapper.map(userDto, UserEntity.class);
        //userDto에 있는(쓴) 내용을 UserEntity에 있는 내용과 mapping시켜라
        userEntity.setEncryptedPwd("encrypted_password");
        userRepository.save(userEntity);

        return null;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity=userRepository.findByUserId(userId);

//        if(userEntity==null) 이부분 빼고 함
//            throw new UsernameNotFoundException("User not found");

        UserDto userDto=new ModelMapper().map(userEntity, UserDto.class);
        //값 변형하고싶으면 mapper 사용, userEntity에 있는 값을 UserDto의 class로 변형

        //1118 추가, rest template 호출
//        String orderUrl=String.format("http://localhost:8000/order-service/%s/orders",userId);
//
//        ResponseEntity<List<ResponseOrder>> orderListResponse=
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                    new ParameterizedTypeReference<List<ResponseOrder>>(){
//                    });
//        //restTemplate.exchange(가져올 링크, Get방식, requestEntity는 null로,해당 타입으로 파라미터 받기?
//
//        List<ResponseOrder> orderList=orderListResponse.getBody();
        //
        //1118 추가, Feign Client 방식, ErrorDecoder
//        log.info("Before call orders microservice");
//        List<ResponseOrder> orderList=orderServiceClient.getOrders(userId);
        //

        //Circuit breaker
        log.info("Before call orders microservice");
        CircuitBreaker circuitBreaker=circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList=circuitBreaker.run(()->orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());
        log.info("After called orders microservice");

        //List<ResponseOrder> orderList=new ArrayList<>();
        userDto.setOrders(orderList);
        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto deleteByUserId(String userId) {
        UserEntity userEntity=userRepository.findByUserId(userId);
        userRepository.delete(userEntity);
        orderServiceClient.deleteUsersOrder(userId);
        return null;
    }

    @Override
    public void createCount(String userId, String musicId) {
        CountDto countDto=new CountDto();
        countDto.setUserId(userId);
        countDto.setCount(0);
        countDto.setMusic(musicId);

        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CountEntity countEntity=mapper.map(countDto, CountEntity.class);
        countRepository.save(countEntity);
    }

    @Override
    public CountDto findByUserIdAndMusic(String userId, String musicId) {
        CountEntity countEntity=countRepository.findByUserIdAndMusic(userId,musicId);
        CountDto countDto=new ModelMapper().map(countEntity, CountDto.class);
        return countDto;
    }

    @Override
    public UserDto patchByUserId(String userId, UserDto userDto) {
        UserEntity updateEntity=userRepository.findByUserId(userId);
        if(userDto.getEmail()!=null)
            updateEntity.setEmail(userDto.getEmail());
        if(userDto.getName()!=null)
            updateEntity.setName(userDto.getName());
        if(userDto.getUserLikeGenre()!=null)
            updateEntity.setUserLikeGenre(userDto.getUserLikeGenre());

        userRepository.save(updateEntity);

        UserDto returnDto=new ModelMapper().map(updateEntity, UserDto.class);
        return returnDto;
    }


}
