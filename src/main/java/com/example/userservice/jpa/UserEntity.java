package com.example.userservice.jpa;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 50, unique = false)//true로하면 하나만 만들어짐
    //unique를 true로 해서 같은 값이 못 들어감
    private String email;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, unique = false)
    private String userId;
    @Column(nullable = false, unique = false)
    private String encryptedPwd;

    @Column(nullable = false, updatable = false, insertable=false)
    @ColumnDefault(value="CURRENT_TIMESTAMP")
    private Date createdDate;

    @ElementCollection //1:n 형식으로 배열 저장
    @Column(nullable = false, unique = false)
    private List<String> userLikeGenre;
}
