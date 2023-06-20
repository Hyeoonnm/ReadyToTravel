package kr.ac.kopo.ReadyToTravel.group;

import kr.ac.kopo.ReadyToTravel.dto.GroupDTO;
import kr.ac.kopo.ReadyToTravel.dto.MemberDTO;
import kr.ac.kopo.ReadyToTravel.dto.plan.PlanDTO;
import kr.ac.kopo.ReadyToTravel.entity.MemberEntity;
import kr.ac.kopo.ReadyToTravel.entity.group.GroupEntity;
import kr.ac.kopo.ReadyToTravel.entity.group.GroupMembership;
import kr.ac.kopo.ReadyToTravel.entity.group.InviteEntity;
import kr.ac.kopo.ReadyToTravel.entity.plan.PlanEntity;
import kr.ac.kopo.ReadyToTravel.member.MemberRepository;
import kr.ac.kopo.ReadyToTravel.plan.PlanCustomRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
public class GroupServiceImpl implements GroupService {
    final GroupRepository groupRepository;
    final GroupMembershipRepository groupMembershipRepository;
    final InviteUrlRepository inviteUrlRepository;
    private final MemberRepository memberRepository;
    private final GroupCustomRepository groupCustomRepository;

    private final PlanCustomRepository planCustomRepository;
    public GroupServiceImpl(GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository, InviteUrlRepository inviteUrlRepository, MemberRepository memberRepository, GroupCustomRepository groupCustomRepository, PlanCustomRepository planCustomRepository) {
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.inviteUrlRepository = inviteUrlRepository;
        this.memberRepository = memberRepository;
        this.groupCustomRepository = groupCustomRepository;
        this.planCustomRepository = planCustomRepository;
    }

    @Override
    public void createGroup(Long planNum, Long leaderNum, String planName) {
        // 그룹 생성
        GroupEntity groupEntity = GroupEntity.builder()
                .createDate(new Date())
                .modifiedDate(new Date())
                .plan(PlanEntity.builder().num(planNum).build())
                .name(planName)
                .build();

        // GROUP 엔티티 저장
        GroupEntity save = groupRepository.save(groupEntity);

        // 그룹에 member 추가
        MemberEntity entity = memberRepository.findById(leaderNum)
                .orElseThrow(() -> new RuntimeException("member 정보가 없습니다"));

        groupMembershipRepository.save(GroupMembership.builder()
                .group(save)
                .member(entity)
                .build());

    }

    @Override
    public void groupAddMember(Long memberNum, String inviteURL) {
        // 초대 URL로 Invite Entity 조회
        InviteEntity invite = inviteUrlRepository.findByInviteURL(inviteURL);

        if (invite == null) {
            System.out.println("존재하지 않는 초대 코드");

        } else if (invite.getExpirationDate().before(new Date())) {
            // 초대 코드가 만료 되었으면 처리

            // 기존 invite 코드 삭제 후, 재생성
            inviteUrlRepository.deleteById(invite.getNum());
            generateInviteCode(invite.getGroupEntity().getGroupNum());
        } else {
            GroupMembership findMembership = groupMembershipRepository
                    .findByGroup_GroupNumAndMember_Num(invite.getGroupEntity().getGroupNum(), memberNum);

            if (findMembership == null) {
                // GroupMembership이 존재하지 않으면 새로 생성하여 그룹에 멤버 추가
                GroupMembership membership = GroupMembership.builder()
                        .group(invite.getGroupEntity())
                        .member(MemberEntity.builder().num(memberNum).build())
                        .build();
                groupMembershipRepository.save(membership);
                System.out.println("멤버 초대 완료");
            } else {
                System.out.println("이미 존재하는 회원");
            }
        }
    }

    @Override
    @Transactional
    public void removeMember(long groupNum, long memberNum) {

        groupMembershipRepository.deleteByGroup_GroupNumAndMember_Num(groupNum, memberNum);
    }

    @Override
    public void updateGroup(GroupDTO group) {

        GroupEntity entity = GroupEntity
                .builder()
                .modifiedDate(group.getModifiedDate())
                .build();


        groupRepository.save(entity);
    }


    @Override
    public String generateInviteCode(long groupNum) {
        //Group PK를 통한 인바이트 엔티티를 조회
        InviteEntity invite = inviteUrlRepository.findByGroupEntity_GroupNum(groupNum);

        //만약에 조회 해온 invete가 null일 때
        if (invite == null) {
            //uuid 생성
            String randomUUID = UUID.randomUUID().toString().substring(0, 8);
            System.out.println(randomUUID);

            //만료 일자 생성 기본값은 현재 날짜 기준 + 1
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 1);
            Date expirationDate = cal.getTime();

            InviteEntity createInvite = InviteEntity.builder()
                    .inviteURL(randomUUID)
                    .groupEntity(GroupEntity.builder().groupNum(groupNum).build())
                    .expirationDate(expirationDate)
                    .build();
            //DB Save 후 url만 리턴
            inviteUrlRepository.save(createInvite);

            return createInvite.getInviteURL();
        }
        //null이 아니면 (존재하는게 있으면)
        return invite.getInviteURL();
    }

    @Override
    @Transactional
    public GroupDTO groupInMember(long groupNum) {
        GroupDTO group = groupCustomRepository.groupInfo(groupNum);

        List<MemberDTO> members = groupCustomRepository.groupInMember(groupNum);
        group.addMember(members);

        PlanDTO plan = planCustomRepository.findByNum(group.getPlanNum());
        group.addPlan(plan);


        return group;
    }

    @Override
    public GroupDTO myGroupList(Long memberNum) {

        GroupDTO myGroupList = groupCustomRepository.myGroupNum(memberNum);
        GroupDTO realGroup = groupCustomRepository.groupInfo(myGroupList.getNum());
        System.out.println("realGroup = " + realGroup.getPlan());

        realGroup.addMember(groupCustomRepository.groupInMember(realGroup.getNum()));
        return realGroup;

    }

}
