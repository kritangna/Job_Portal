package com.luv2code.jobportal.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class FileDownloadUtil {

    private Path foundfile;

    public Resource getFileAsResource(String downloadDir, String fileName) throws IOException
    {
        Path path = Paths.get(downloadDir, fileName);
        Files.list(path).forEach(file ->
        {
            if(file.getFileName().toString().startsWith(fileName))
            {
                foundfile = file;
            }
        });

        if(foundfile != null)
        {
            return new UrlResource(foundfile.toUri());
        }
        return null;
    }
}
