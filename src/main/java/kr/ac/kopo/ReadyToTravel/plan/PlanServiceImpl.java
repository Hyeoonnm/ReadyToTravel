package kr.ac.kopo.ReadyToTravel.plan;

import kr.ac.kopo.ReadyToTravel.dto.plan.PlanDTO;
import kr.ac.kopo.ReadyToTravel.dto.plan.LonLatDTO;
import kr.ac.kopo.ReadyToTravel.entity.group.GroupMembership;
import kr.ac.kopo.ReadyToTravel.entity.plan.LonLatEntity;
import kr.ac.kopo.ReadyToTravel.entity.plan.PlanEntity;
import kr.ac.kopo.ReadyToTravel.group.GroupMembershipRepository;
import kr.ac.kopo.ReadyToTravel.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final LonLatRepository lonLatRepository;
    private final MemberRepository memberRepository;

    @Override
    public PlanDTO viewPlan(long planNum) {
        PlanEntity planEntity = planRepository.findByNum(planNum);
        List<LonLatEntity> lonLatEntity = lonLatRepository.findAllByPlanEntityNum(planNum);

        PlanDTO planDTO = planEntity.convertToDTO(planEntity, planNum, lonLatEntity);

        return planDTO;
    }

    @Override
    public Long createPlan(PlanDTO plan) {
        // Client가 보낸 PlanDTO entity에 save
        PlanEntity planConvertToEntity = plan.convertToEntity(plan, plan.getLeaderNum());
        PlanEntity planEntity = planRepository.save(planConvertToEntity);

        // Client가 보낸 LonLatDTO entity에 save
        for (int i = 0; i < plan.getLonLatList().size(); i++) {
            LonLatDTO lonLatDTO = new LonLatDTO();
            LonLatEntity lonLatEntities = lonLatDTO.convertToEntity(plan.getLonLatList().get(i), planEntity.getNum());
            lonLatRepository.save(lonLatEntities);
        }

        return planEntity.getNum();
    }

    @Override
    public PlanEntity getItem(Long num) {
        return null;
    }

    @Override
    public void updatePlan(PlanDTO plan) {

    }

    @Override
    @Transactional
    public void removePlan(Long num) {
        planRepository.deleteById(num);
    }
}
