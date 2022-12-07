package com.example.userservice.controller;

import com.example.userservice.dto.CountDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseCount;
import com.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserController {
    private UserService userService;
    private Environment env;//yml 파일 내용
    public UserController(Environment env,UserService us){//생성자
        this.env=env;
        this.userService=us;
    }

    @GetMapping("/health_check")//uri
    public String status(){
//        return String.format("%s",env.getProperty("configWelcome.message"));
//        /*env에 있는 bootstrap으로 이동
//        >localhost:8888에 감
//        >야믈파일 이름을 따라 ecommerce로 이동
//        >configWelcome을 읽어옴*/
        return String.format("It's Working in User Service on Port %s",env.getProperty("local.server.port"));
        //data 자체를 return
    }
//    public String status(HttpServletRequest request){
//        return String.format("It's Working in User Service on Port %s",request.getServerPort());
//    }

    @GetMapping("/welcome")
    public String welcome(){
        return env.getProperty("greeting.message");
    }
    //이게 하나의 api

    @PostMapping("/users")
    public String createUser(@RequestBody RequestUser user){
        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto=mapper.map(user, UserDto.class);
        userService.createUser(userDto);
        return "Create user method is called";
    }

    @GetMapping(value="/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList=userService.getUserByAll();

        List<ResponseUser> result=new ArrayList<>();
        userList.forEach(v->{
            result.add(new ModelMapper().map(v, ResponseUser.class));
            //리스트 안의 모든 값(ResponseUser type이 아님)을 ResponseUser.class 형태로
        });
        
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    @GetMapping(value="/users/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
        UserDto userDto=userService.getUserByUserId(userId);
        ResponseUser returnValue=new ModelMapper().map(userDto, ResponseUser.class);
        //특정 아이디에 있는 정보를 가져와서 Dto에 저장하고 ResponseUser.class의 형태로 바꾸는 것
        
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
        //200번, 정상적으로 반환이 되면 body를 출력해라?
    }

    @DeleteMapping(value="/users/{userId}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public String deleteUser(@PathVariable("userId") String userId){
        userService.deleteByUserId(userId);
        return userId+" is deleted.";
    }

    @GetMapping(value="/{productId}/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseCount> countUser(
            @PathVariable("userId") String userId,
            @PathVariable("productId") String productId){
        CountDto countDto=userService.findByUserIdAndProduct(userId,productId);
        ResponseCount returnValue=new ModelMapper().map(countDto, ResponseCount.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    @PatchMapping(value = "/users/{userId}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseUser> patchUser(@PathVariable("userId") String userId,
                                                  @RequestBody RequestUser user){
        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto=userService.patchByUserId(userId,mapper.map(user, UserDto.class));
        ResponseUser value=mapper.map(userDto,ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(value);
    }
}
