package com.cl.comused;

import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Created by L on 2017/5/27.
 */
public class HttpComUsed {


//	public static CloseableHttpClient cHttpClient_http=HttpClients.createDefault();

    /**
     * 发送https时要这样获取http客户端
     */
    public static CloseableHttpClient getHttpsClient(){
        Security.addProvider(new BouncyCastleProvider());
        // 重写验证方法，取消检测SSL：
        X509TrustManager x509TrustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        };
        // 获取TLS安全协议上下文
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] { x509TrustManager }, null);
            SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", scsf).build();
            PoolingHttpClientConnectionManager phcConnManager = new PoolingHttpClientConnectionManager(sfr);
            CloseableHttpClient cHttpClient_https= HttpClients.custom().setConnectionManager(phcConnManager).setDefaultRequestConfig(requestConfig).build();
            return cHttpClient_https;
        } catch (Exception e) {
//            logger.error("https客户端初始化有误：", e);
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 执行请求，不管是http请求还是https请求都是这个模式，有所不同的是httpClient不同，https的客户端要用上面方法获取
     * @author chenlei
     * @date 2018年9月28日
     * @param cHttpClient http或https客户端
     * @param url 请求地址
     * @param paramDatas 请求参数键值对列表
     * @param jsession jsessionid
     * @return CloseableHttpResponse
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse exeRequest(CloseableHttpClient cHttpClient, String url, Map<String, String> paramDatas, String jsession) throws ClientProtocolException, IOException{
        HttpPost httpPost=new HttpPost(url);
        // 设置请求参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(paramDatas!=null){
            Set<Entry<String,String>> entrySet = paramDatas.entrySet();
            for(Iterator<Entry<String, String>> iterator = entrySet.iterator();iterator.hasNext();){
                Entry<String, String> entry = iterator.next();
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
        httpPost.setEntity(reqEntity);
        if(jsession!=null){
            httpPost.setHeader("Cookie", "JSESSIONID=" + jsession);
        }

        CloseableHttpResponse closeableHttpResponse = cHttpClient.execute(httpPost);
//		closeableHttpResponse.close();
//		cHttpClient.close();
        return closeableHttpResponse;
    }
}
