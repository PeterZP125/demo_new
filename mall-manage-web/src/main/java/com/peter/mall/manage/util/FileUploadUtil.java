package com.peter.mall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileUploadUtil {

    public static String fileUpload(MultipartFile multipartFile) {
        String file = FileUploadUtil.class.getResource("/FastDFS.properties").getFile();
        try {
            if (multipartFile != null) {
                ClientGlobal.init(file);
                TrackerClient trackerClient = new TrackerClient();
                TrackerServer trackerServer = trackerClient.getConnection();
                StorageClient storageClient = new StorageClient(trackerServer, null);
                String originFileName = multipartFile.getOriginalFilename();
                String fileExtName = originFileName.substring(originFileName.lastIndexOf(".") + 1);
                String[] retMsg = storageClient.upload_file(multipartFile.getBytes(), fileExtName, null);
                StringBuilder sb = new StringBuilder(80);
                for (int i = 0; i < retMsg.length; i++) {
                    sb.append("/");
                    sb.append(retMsg[i]);
                }
                String url = "http://192.168.125.100";
                url += sb.toString();
                return url;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return "";
    }
}
