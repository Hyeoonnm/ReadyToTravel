package kr.ac.kopo.ReadyToTravel.board;

import kr.ac.kopo.ReadyToTravel.board.attach.BoardAttachCustomRepository;
import kr.ac.kopo.ReadyToTravel.board.attach.BoardAttachRepository;
import kr.ac.kopo.ReadyToTravel.board.reply.ReplyCustomRepository;
import kr.ac.kopo.ReadyToTravel.dto.BoardDTO;
import kr.ac.kopo.ReadyToTravel.dto.ReplyDTO;
import kr.ac.kopo.ReadyToTravel.entity.attach.BoardAttachEntity;
import kr.ac.kopo.ReadyToTravel.entity.board.BoardEntity;
import kr.ac.kopo.ReadyToTravel.util.FileUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {
    final BoardRepository repository;
    final BoardAttachRepository boardAttachRepository;
    final BoardCustomRepository boardCustomRepository;
    final ReplyCustomRepository replyCustomRepository;

    final BoardAttachCustomRepository boardAttachCustomRepository;

    public BoardServiceImpl(BoardRepository repository, BoardAttachRepository boardAttachRepository, BoardCustomRepository boardCustomRepository, ReplyCustomRepository replyCustomRepository, BoardAttachCustomRepository boardAttachCustomRepository) {
        this.repository = repository;
        this.boardAttachRepository = boardAttachRepository;
        this.boardCustomRepository = boardCustomRepository;
        this.replyCustomRepository = replyCustomRepository;
        this.boardAttachCustomRepository = boardAttachCustomRepository;
    }

    @Override
    public long create(BoardDTO boardDTO) {
        BoardEntity entity = BoardDTO.convertToEntity(boardDTO);
        Long boardNum = repository.save(entity).getBoardNum();
        if (boardDTO.getMultipartFiles() != null && !boardDTO.getMultipartFiles().isEmpty()) {
            List<BoardAttachEntity> attachEntities = new ArrayList<>();
            for (int i = 0; i < boardDTO.getMultipartFiles().size(); i++) {
                MultipartFile attach = boardDTO.getMultipartFiles().get(i);
                String filename = FileUpload.fileUpload(attach, 1);
                if (filename != null) {
                    BoardAttachEntity attachEntity = new BoardAttachEntity();
                    attachEntity.setFileName(filename);
                    attachEntity.setBoardEntity(BoardEntity.builder().boardNum(boardNum).build());
                    attachEntities.add(attachEntity);
                }
            }
            boardAttachRepository.saveAll(attachEntities);
        }
        return boardNum;
    }

//    @Override
//    public List<BoardDTO> boardList() {
//
//        List<BoardDTO> boardList = boardCustomRepository.boardList();
//
//        return boardList;
//    }

    @Override
    @Transactional
    public BoardDTO detail(Long boardNum) {

        // 게시글 상세 정보 조회
        BoardDTO detail = boardCustomRepository.getBoardDetail(boardNum);

        //게시글에 포함된 댓글의 정보 조회
        List<ReplyDTO> replies = replyCustomRepository.getReplies(boardNum);
        if (replies != null) {
            detail.setReplies(replies);
        }
        //게시글 첨부파일 조회
        List<String> attaches = boardAttachCustomRepository.findByFileNameByBoardNum(boardNum);
        detail.setFilename(attaches);

        return detail;
    }

    @Override
    @Transactional
    public BoardDTO smallDetail(Long boardNum) {
        BoardDTO board = boardCustomRepository.smallDetail(boardNum);

        board.setFilename(boardAttachCustomRepository.findByFileNameByBoardNum(boardNum));

        return board;
    }

    @Override
    public Page<BoardDTO> boardList(Pageable pageable) {
        return boardCustomRepository.boardList(pageable);
    }

    @Override
    public List<BoardDTO> myBoardList(Long num) {
        return boardCustomRepository.myBoardList(num);
    }


    @Override
    @Transactional
    public void update(BoardDTO boardDTO) {
        BoardEntity boardEntity = repository.findById(boardDTO.getBoardNum())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시글 번호"));

        boardEntity.setBoardName(boardDTO.getBoardName());
        boardEntity.setBoardContent(boardDTO.getBoardContent());


        if (!boardDTO.getMultipartFiles().isEmpty() && !boardDTO.getMultipartFiles().get(0).getOriginalFilename().isEmpty()) {
            List<BoardAttachEntity> boardAttachEntity = boardAttachRepository.deleteByBoardEntityBoardNum(boardDTO.getBoardNum());

            for (BoardAttachEntity attachEntity : boardAttachEntity) {
                FileUpload.fileRemove(attachEntity.getFileName(), 1);
            }

            List<BoardAttachEntity> attachEntities = new ArrayList<>();

            for (MultipartFile attach : boardDTO.getMultipartFiles()) {
                String filename = FileUpload.fileUpload(attach, 1);

                if (filename != null) {
                    BoardAttachEntity attachEntity = new BoardAttachEntity();
                    attachEntity.setFileName(filename);
                    attachEntity.setBoardEntity(BoardEntity.builder().boardNum(boardDTO.getBoardNum()).build());
                    attachEntities.add(attachEntity);
                }
            }

            boardAttachRepository.saveAll(attachEntities);
        }
    }


    @Override
    public void delete(Long boardNum) {
        List<BoardAttachEntity> attachEntity = boardAttachRepository.findByBoardEntityBoardNum(boardNum);

        for (int i = 0; i < attachEntity.size(); i++) {
            FileUpload.fileRemove(attachEntity.get(i).getFileName(), 1);
        }

        repository.deleteById(boardNum);
    }


}

