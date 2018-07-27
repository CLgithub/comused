package com.cl.comused;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by chenlei on 2017/9/20.
 */
public class FtpComUserd {

    public static void main(String[] args){
        List<File> files=new ArrayList<File>();
        files.add(new File("/Users/l/develop/ultrapower/ultrapowerBin/31-/test/"));
        ftpUpLoadFD(files);
    }

    /**
     * ftp上传文件夹
     * @author L
     * @date 2018-7-27
     * @param files
     */
    public static void ftpUpLoadFD(List<File> files){
        String ftpUrl = "192.168.3.231"; //ftp服务器地址
        int ftpPort = 21; //ftp服务端口
        String ftpUserName = "username"; //ftp登录名
        String ftpPassword = "pwd"; //ftp登录密码
        String ftpPath = "/other/"; //ftp路径

        FileInputStream fiStream = null;
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("UTF-8");
            int reply;
            ftpClient.connect(ftpUrl, ftpPort);// 连接FTP服务器
            boolean login = ftpClient.login(ftpUserName, ftpPassword);    //登录
            if(login){
                reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                }
                boolean setFileType = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                boolean changeWorkingDirectory = ftpClient.changeWorkingDirectory(ftpPath);
                if(changeWorkingDirectory){
                    ftpClient.setFileTransferMode(ftpClient.BINARY_FILE_TYPE);
                    ftpClient.enterLocalPassiveMode();  //设置本地为被动模式
                    upDir(files,ftpClient,ftpPath,fiStream);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fiStream != null)
                    fiStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ftpClient != null)
                        ftpClient.logout();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ftpClient != null)
                            ftpClient.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void upDir(List<File> files, FTPClient ftpClient, String ftpPath, FileInputStream fiStream) throws IOException {
        int reply;
        for (File file : files) {
            if(file.isDirectory()){
                boolean b = ftpClient.makeDirectory(ftpPath+file.getName());
                reply=ftpClient.getReplyCode();     //得到返回参数
                System.out.println("创建文件夹："+file.getName()+",是否成功："+b+","+reply);
                upDir(Arrays.asList(file.listFiles()),ftpClient,ftpPath+file.getName()+"/",fiStream);
            }else if(!".DS_Store".equals(file.getName())){
                fiStream = new FileInputStream(file);
                boolean storeFile = ftpClient.storeFile(ftpPath+file.getName(), fiStream);
                reply=ftpClient.getReplyCode();     //得到返回参数
                System.out.println("上传"+file.getName()+"是否成功：" + storeFile+","+reply);
            }
        }
    }

    /**
     * ftp下载文件
     * @author L
     * @date 2017-5-27
     */
    public static List<File> ftpDownload() {
        FTPClient ftpClient = null;
        OutputStream oStream = null;
        String ftpServiceIp = "192.168.1.2";
        int ftpServicePort = 22;
        String ftpServiceUser = "userName"; //ftp登录名
        String ftpServicePassword = "password"; //ftp登录密码
        String ftpServicePath = "/data1/oss"; //ftp路径
        String localPath = "~/download/";  //文件存放的本地目录
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.connect(ftpServiceIp, ftpServicePort);    //连接
            ftpClient.enterLocalPassiveMode();
            List<File> list = new ArrayList<File>();
            boolean isLogin = ftpClient.login(ftpServiceUser, ftpServicePassword);    //登录
            if (isLogin) {
                FTPFile[] files = ftpClient.listFiles(ftpServicePath);
                for (int i = files.length - 24; i < files.length; i++) {
                    FTPFile ftpFile = files[i];
                    ftpClient.changeWorkingDirectory(ftpServicePath);    //转移到FTP服务器目录
                    File localFile = new File(localPath + ftpFile.getName());
                    oStream = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(ftpFile.getName(), oStream);
                    oStream.flush();
                    oStream.close();
                    list.add(localFile);
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (oStream != null) {
                    oStream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (ftpClient != null) {
                        ftpClient.logout();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
