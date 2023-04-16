package kr.ac.kopo.ReadyToTravel.board;

import kr.ac.kopo.ReadyToTravel.dto.BoardDTO;
import kr.ac.kopo.ReadyToTravel.entity.attach.BoardAttachEntity;
import kr.ac.kopo.ReadyToTravel.entity.board.BoardEntity;
import kr.ac.kopo.ReadyToTravel.util.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {
    final BoardRepository repository;
    final BoardAttachRepository boardAttachRepository;

    public BoardServiceImpl(BoardRepository repository, BoardAttachRepository boardAttachRepository) {
        this.repository = repository;
        this.boardAttachRepository = boardAttachRepository;
    }

    @Override
    public void create(BoardDTO boardDTO) {
        // 첨부파일을 저장할 Board Attach List
        List<BoardAttachEntity> attachEntities = new ArrayList<>();

        // Dto to entity
        BoardEntity entity = BoardDTO.convertToEntity(boardDTO);

        // entity 저장하고 boardNum 가져옴
        Long boardNum = repository.save(entity).getBoardNum();

        for (int i = 0; i < boardDTO.getMultipartFile().size(); i++) {
            MultipartFile attach = boardDTO.getMultipartFile().get(i);
            String filename = FileUpload.fileUpload(attach);
//여기서 사용 했으니깐 여기쪽 한번 보셔용
            if (filename != null) {
                BoardAttachEntity attachEntity = new BoardAttachEntity();
                attachEntity.setFileName(filename);
                attachEntity.setBoardEntity(BoardEntity.builder().boardNum(boardNum).build());
                attachEntities.add(attachEntity);
            }
        }

        // attach 저장
        boardAttachRepository.saveAll(attachEntities); // 모든 BoardAttachEntity 저장
    }

    @Override
    public List<BoardDTO> findAll() {
         return repository.findAll().stream()
                 .map(boardEntity ->{
                     BoardDTO boardDTO = new BoardDTO();
                     boardDTO.setBoardNum(boardEntity.getBoardNum());
                     boardDTO.setBoardName(boardEntity.getBoardName());
                     boardDTO.setBoardDateCreate(boardEntity.getBoardDateCreate());
                     return boardDTO;
                 })
                 .collect(Collectors.toList());
    }
//    List<BoardEntity> entityList = repository.findAll();
//    List<BoardDTO> boardDTOs = new ArrayList<>();
//        for (BoardEntity boardEntity : entityList) {
//        BoardDTO boardDTO = BoardDTO.convertToDTO(boardEntity);
//        boardDTOs.add(boardDTO);
//    }
//        return boardDTOs;

    @Override
    public BoardDTO findById(Long boardNum) {
        BoardEntity entity = repository.findById(boardNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시번호") );
        return BoardDTO.convertToDTO (entity);
    }

    @Override
    public BoardDTO update(BoardDTO boardDTO,Long boardNum) {
        BoardEntity entity = repository.findById(boardNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시번호:" + boardDTO.getBoardNum()));

        entity.setBoardName(boardDTO.getBoardName());
        entity.setBoardContent(boardDTO.getBoardContent());

        repository.save(entity);

        return BoardDTO.convertToDTO (entity);
    }

    @Override
    public void delete(Long boardNum) {
        repository.deleteById(boardNum);
    }
}

