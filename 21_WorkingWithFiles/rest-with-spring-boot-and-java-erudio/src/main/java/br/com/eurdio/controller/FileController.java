package br.com.eurdio.controller;


import br.com.eurdio.data.vO.v1.UploadFileRespondeVO;
import br.com.eurdio.services.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Tag(name = "File Endpoint")
@RestController
@RequestMapping("/api/file/v1")
public class FileController {

        private Logger logger = Logger.getLogger(FileController.class.getName());

        @Autowired
        private FileStorageService service;

        @PostMapping("/upload-file")
        public UploadFileRespondeVO uploadFile(@RequestParam("file") MultipartFile file){
            logger.info("Storing file to disk");
            String fileName = service.storeFile(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/file/v1/download-file/")
                    .path(fileName)
                    .toUriString();

            return new UploadFileRespondeVO(
                    fileName, fileDownloadUri, file.getContentType(), file.getSize());
        }

    @PostMapping("/upload-multiple-files")
    public List<UploadFileRespondeVO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files){
        logger.info("Storing files to disk");
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

}
