package com.example.demo.file;

import ch.qos.logback.core.util.StringUtil;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {
    private FileUtils(){}

    public static byte[] readFileFromLocation(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return new byte[0];
        }
        try {
            Path filePath = new File(fileUrl).toPath();
            //Paths.get(fileUrl)
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.warn("failed to read file Bytes from path {}", fileUrl, e);
        }
        return new byte[0];

    }

}
