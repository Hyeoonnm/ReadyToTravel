package kr.ac.kopo.ReadyToTravel.entity;

import kr.ac.kopo.ReadyToTravel.dto.MemberDTO;
import kr.ac.kopo.ReadyToTravel.entity.group.GroupEntity;
import kr.ac.kopo.ReadyToTravel.entity.group.GroupMembership;
import kr.ac.kopo.ReadyToTravel.util.PassEncode;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Member")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_num")
    private Long num;
    // 아이디

    @Column(name = "member_id")
    private String memberId;

    // 이름
    @Column
    private String name;

    // 비밀번호
    @Column
    private String password;

    // 프로필 사진
    @Column(name = "profile_img")
    private String profileIMG;

    // 가입 일자
    @Column
    private Date signupDate;

    //이메일
    @Email
    @Column
    private String email;

    //핸드폰 번호
    @Column
    private String phoneNum;

    public void saveProfile(String name,String password){
        this.password = password;
        this.name = name;

    }

    public void saveProfileIMG(String profileIMG){
        this.profileIMG = profileIMG;
    }

}


