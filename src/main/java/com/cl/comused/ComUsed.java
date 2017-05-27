package com.cl.comused;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by L on 2017/5/27.
 */
public class ComUsed {

    /**
     * ftp 上传
     * @author L
     * @date 2017-5-27
     */
    public static void ftpUpload() {
        String ftpUrl = "192.168.1.2"; //ftp服务器地址
        int ftpPort = 22; //ftp服务端口
        String ftpUserName = "userName"; //ftp登录名
        String ftpPassword = "password"; //ftp登录密码
        String ftpPath = "/data1/oss"; //ftp路径

        List<File> files = new ArrayList<File>();
        FileInputStream fiStream = null;
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("UTF-8");
            int reply;
            ftpClient.connect(ftpUrl, ftpPort);// 连接FTP服务器
            boolean login = ftpClient.login(ftpUserName, ftpPassword);    //登录
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            boolean setFileType = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            boolean changeWorkingDirectory = ftpClient.changeWorkingDirectory(ftpPath);
            for (File file : files) {
                fiStream = new FileInputStream(file);
                boolean storeFile = ftpClient.storeFile(file.getName(), fiStream);
                System.out.println("上传是否成功：" + storeFile);
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

    /**
     * http请求
     */
    public static void httpRequest() {
    }

    /**
     * https请求
     */
    public static void httpsRequest() {
        Security.addProvider(new BouncyCastleProvider());
        // 重写验证方法，取消检测SSL：
        X509TrustManager x509TrustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
        // 获取TLS安全协议上下文
        SSLContext context = null;
        CloseableHttpClient cHttpClient = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{x509TrustManager}, null);
            SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", scsf).build();
            PoolingHttpClientConnectionManager phcConnManager = new PoolingHttpClientConnectionManager(sfr);
            cHttpClient = HttpClients.custom().setConnectionManager(phcConnManager).setDefaultRequestConfig(requestConfig).build();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String jsid = "";     //jsessionid
        String uri = "https://xxx.xxx/xxx/xxx.htm";   //请求目标方法
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Cookie", "JSESSIONID=" + jsid);
        //设置公共头
        httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        httpPost.setHeader("Cache-Control", "max-age=0");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Host", "xxx.xxx.xxx.xxx");
        httpPost.setHeader("Upgrade-Insecure-Requests", "1");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        /************设置请求参数start**************************************/
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("deviceName", "所有设备"));
        nvps.add(new BasicNameValuePair("clusterName", "所有用户组"));
        //...
        HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
        httpPost.setEntity(reqEntity);
        /************设置请求参数end**************************************/

        //发送请求获取相应信息
        try {
            CloseableHttpResponse cHttpResponse = cHttpClient.execute(httpPost);
            String resStr = EntityUtils.toString(cHttpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //sql有记录就修改，无记录就插入
    public static void sql1(Connection conn) {
        // 当T_Cen_alarm_Msgworder表已经有了和新纪录fp0～fp3都相同的，就写该原记录的sheet_no和result_string，如果没有，就插入新纪录
        String sql = "merge into T_Cen_alarm_Msgworder a"
                + " using (select ? as fp0,? as fp1,? as fp2,? as fp3,? as sheet_no,? as result_string from dual) b"
                + " on (a.fp0=b.fp0 and a.fp1=b.fp1 and a.fp2=b.fp2 and a.fp3=b.fp3)"
                + " when matched then"
                + " update set a.sheet_no=b.sheet_no,a.result_string=b.result_string where a.fp0=b.fp0 and a.fp1=b.fp1 and a.fp2=b.fp2 and a.fp3=b.fp3"
                + " when not matched then"
                + " insert values (?,?,?,?,?,?)";
        PreparedStatement pStatement1 = null;
        try {
            pStatement1 = conn.prepareStatement(sql);
            pStatement1.setString(1, "fp0");
            pStatement1.setString(2, "fp1");
            pStatement1.setString(3, "fp2");
            pStatement1.setString(4, "fp3");
            pStatement1.setString(5, "sheet_no");
            pStatement1.setString(6, "result_string");
            pStatement1.setString(7, "fp0");
            pStatement1.setString(8, "fp1");
            pStatement1.setString(9, "fp2");
            pStatement1.setString(10, "fp3");
            int i = pStatement1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pStatement1 != null)
                    pStatement1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
