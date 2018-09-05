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
public class HttpComUsed {



    /**
     * http请求
     */
    public static void httpRequest() {
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse response=null;
        String paramData="{'a':123,'b':'c'}";
        String url="http://xxxx.xxx.xxx";
        String jsession="";
        try {
            // 设置请求参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("paramData",paramData));
            HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(reqEntity);
            httpPost.setHeader("Cookie", "JSESSIONID=" + jsession);

            // 执行请求
            response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(httpClient!=null) httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(response!=null)response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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




}
