package kr.ac.kopo.ReadyToTravel.board.attach;

import kr.ac.kopo.ReadyToTravel.entity.attach.BoardAttachEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardAttachRepository extends JpaRepository<BoardAttachEntity, Long> {
    List<BoardAttachEntity> deleteByBoardEntityBoardNum(long boardNum);
    List<BoardAttachEntity> findByBoardEntityBoardNum(long boardNum);
}
