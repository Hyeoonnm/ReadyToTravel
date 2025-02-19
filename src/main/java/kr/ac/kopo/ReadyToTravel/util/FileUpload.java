package kr.ac.kopo.ReadyToTravel.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.aspectj.asm.internal.CharOperation.lastIndexOf;

public class FileUpload {
    static String path = "D:\\upload\\";

    static String boardPath = "board\\";

    static String profilePath = "profile\\";


    /**
     * @param attach
     * @return fileUpload 는 사용자에게 받은 Attach 첨부파일 (사진, 동영상 등) 을 로컬 환경에 저장하는 메소드입니다.
     * 파일을 파라미터로 받아 저장 후 파일의 이름(String fileName)만 리턴 받습니다.
     * type이 1이면 게시판 사진, 2면 프로필 사진 ...
     */
    public static String fileUpload(MultipartFile attach, int type) {
        String removePath = path;

        if (attach != null && !attach.isEmpty()) {
            try {
                String filename = UUID.randomUUID().toString().substring(0, 8);
                String ext = attach.getOriginalFilename().substring(attach.getOriginalFilename().lastIndexOf(".") + 1);

                if (type == 1) {
                    attach.transferTo(new File(removePath + boardPath + filename + "." + ext));
                } else if (type == 2) {
                    attach.transferTo(new File(removePath + profilePath + filename + "." + ext));
                }

                return filename + "." + ext;
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return null;
    }

    public static void fileRemove(String filename, int type) {
        String removePath = path;

        if (type == 1) {
            removePath += boardPath + filename;
        } else if (type == 2) {
            removePath += profilePath + filename;
        }

        File file = new File(removePath);
        if (file == null) {
            return;
        }

        file.delete();

    }
}