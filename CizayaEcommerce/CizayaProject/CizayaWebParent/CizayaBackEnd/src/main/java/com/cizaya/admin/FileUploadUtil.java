package com.cizaya.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class FileUploadUtil {
    //vi du gi log
//	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    // B1 cap nhap bieu mau nguoi dung de cho phep tai tep len
    // vaf hien thi hinh anh thu nho cua hinh anh - da lam o user_form

    // B2 luu cac tep da tai len o file FileUploadUtil


    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        // tao 1 thu muc vuot qua thu muc tu uploadDir
        Path uploadPath = Paths.get(uploadDir);

        // neu uploadPath ko ton tai thi tao thu muc uploadPath
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // test thu tai nguyen trong java
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // tao duong dan cua tep cos lien quan den thu muc tai len fileName duoc turyen
            // tu tham so cua saveFile
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Could not save file: " + fileName, ex);
        }
    }

    // clearn lam sach thu muc
    public static void cleanDir(String dir) {
        Path dirPath = Paths.get(dir);

        try {
            Files.list(dirPath).forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        //LOGGER.error("Could not delete file: " + file);
                        System.out.print("Could not delete file: " + file);
                    }
                }
            });
        } catch (IOException ex) {
            //LOGGER.error("Could not delete file: " + file);
            System.out.print("Could not list derectory: " + dirPath);
        }
    }

}
