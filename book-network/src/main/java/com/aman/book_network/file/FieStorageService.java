package com.aman.book_network.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aman.book_network.book.Book;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class FieStorageService {
    @Value("${application.file.upload.photos-output-path}")
     private String fieUploadPath;
    public String saveFile(
        @Nonnull MultipartFile sourceFile,
        @Nonnull Book book,
        @Nonnull Integer userId) {
      final String fileUploadSubPath="users"+File.separator+userId;
     return uploadFile(sourceFile,fileUploadSubPath);
    }
    private String uploadFile(
        @Nonnull MultipartFile sourceFile, 
        @Nonnull String fileUploadSubPath) {
        final String finalUploadPath=fieUploadPath+File.separator+fileUploadSubPath;
        File targetFolder=new File(finalUploadPath);
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("Failed to create the target folder");
                return null;
            }
        }

      final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
      String targetFilePath=finalUploadPath+File.separator+System.currentTimeMillis()+"."+fileExtension;
      Path targetPath=Paths.get(targetFilePath);
      try{
           Files.write(targetPath,sourceFile.getBytes());
           log.info("File saved to "+targetFilePath);
      }
      catch(IOException ex){
          log.error("File was not saved",ex);
      }
      return targetFilePath;
    }
    private String getFileExtension(String fileName) {
        if(fileName==null || fileName.isEmpty()){
           return "";
        }

        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex==-1){
            return "";
        }
      return fileName.substring(lastDotIndex+1);
    }
    
}
