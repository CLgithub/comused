package com.cl.comused;

import com.cl.common.servicce.BaseServiceImpl;
import com.cl.common.utils.JDBCUtilHikariCP;
import org.apache.commons.dbutils.QueryRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by chenlei on 2017/9/20.
 */
public class SqlComUserd {

    //sql有记录就修改，无记录就插入
    public static void sql1(Connection conn) {
        // 当t_smartDNS_domain表已经存在dname（主键）的记录时，就更新新纪录，否则就插入
        String sql=" merge into t_smartDNS_domain a "
                +" using (select ? as dname,? as version,? as source,? as responsecode, "
                +" ? as minttl, ? as solutiontime, ? as province, to_date(?,'yyyy-MM-dd hh24:mi:ss') as updatetime from dual) b "
                +" on ( a.dname=b.dname ) "
                +" when matched then "
                +" update set a.version=b.version, a.source=b.source, "
                +" a.responseCode=b.responseCode, a.minTtl=b.minTtl, a.solutionTime=b.solutionTime, "
                +" a.province=b.province, a.updateTime=b.updateTime where a.dname=b.dname "
                +" when not matched then "
                +" insert values (?,?,?,?,?,?,?,to_date(?,'yyyy-MM-dd hh24:mi:ss')) ";
        PreparedStatement pStatement1 = null;
        try {
            pStatement1.setString(1, "dname");
            pStatement1.setString(2, "version");
            pStatement1.setString(3, "source");
            pStatement1.setString(4, "responseCode");
            pStatement1.setString(5, "minTtl");
            pStatement1.setString(6, "solutionTime");
            pStatement1.setString(7, "province");
            pStatement1.setString(8, "updateTime");

            pStatement1.setString(9, "dname");
            pStatement1.setString(10, "version");
            pStatement1.setString(11, "source");
            pStatement1.setString(12, "responseCode");
            pStatement1.setString(13, "minTtl");
            pStatement1.setString(14, "solutionTime");
            pStatement1.setString(15, "province");
            pStatement1.setString(16, "updateTime");
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

    /**
     * 根据sql将查询结果存储到文件中
     * @param sql 查询语句
     * @param csvFile csv文件
     * @param separator 分隔符
     * @param head 是否需要列名
     */
    public static void getFileBysql(String sql, File csvFile, String separator,  boolean head){
        BufferedWriter bufferedWriter=null;
        try {
            bufferedWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile)));
            BaseServiceImpl baseService = new BaseServiceImpl();
            QueryRunner queryRunner = JDBCUtilHikariCP.getQueryRunner();
            if(head){
                // 过去头信息
                String pageSql = baseService.getPageBeanSqlOracle(sql, 1, 1);
                List<Map<String, Object>> listMap1 = baseService.selectListMapBySql(queryRunner, pageSql);
                Map<String, Object> map = listMap1.get(0);
                Set<Entry<String, Object>> entrySet = map.entrySet();
                StringBuffer sBuffer2 = new StringBuffer();
                for (Iterator<Entry<String, Object>> iterator = entrySet.iterator(); iterator.hasNext();) {
                    Entry<String, Object> entry = iterator.next();
                    sBuffer2.append(entry.getKey());
                    sBuffer2.append(separator);
                }
                String strT = sBuffer2.toString();
                strT = strT.substring(0, strT.length() - separator.length());
                bufferedWriter.write(strT+System.getProperty("line.separator"));
            }
            // 获取各行数据
            int batchSize=5000;
            int total=baseService.getTotlaBySql(queryRunner,sql);
            for(int i=1;i<=total/batchSize+1;i++){
                String pageSql = baseService.getPageBeanSqlOracle(sql, i, batchSize);
                List<Object[]> list = baseService.selectListArrayBySql(queryRunner, pageSql);
                for (Object[] objects:list) {
                    String strLin = Arrays.toString(objects);
                    strLin=strLin.replace("null", "");
                    strLin = strLin.substring(1, strLin.length() - 1);
                    strLin = strLin.replace(", ", separator);
                    bufferedWriter.write(strLin+System.getProperty("line.separator"));
                }
                bufferedWriter.flush();
            }
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedWriter!=null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据sql将数据封装到csv文件
     * @author chenlei
     * @date 2018年5月31日
     * @param sql sql语句
     * @param csvFile csv文件
     * @param separator 分隔符
     * @param head 是否将头数据封装到文件
     * @param batchSize 每次查询大小
     * @return
     */
    public static String getFileBySql(String sql, File csvFile, String separator, boolean head, int batchSize) {
        BufferedWriter bufferedWriter = null;
        String strT="";
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile),"utf-8"));
            BaseServiceImpl baseService = new BaseServiceImpl();
            QueryRunner queryRunner = JDBCUtilHikariCP.getQueryRunner();
            //查询头数据
            String pageSql1 = baseService.getPageBeanSqlOracle(sql, 1, 1);
            List<Map<String, Object>> listMap1 = baseService.selectListMapBySql(queryRunner, pageSql1);
            Map<String, Object> map = (Map)listMap1.get(0);
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            StringBuffer sBuffer2 = new StringBuffer();
            Iterator iterator = entrySet.iterator();

            while(iterator.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)iterator.next();
                sBuffer2.append((String)entry.getKey());
                sBuffer2.append(separator);
            }

            strT = sBuffer2.toString();
            strT = strT.substring(0, strT.length() - separator.length());
            strT = strT.substring(0, strT.lastIndexOf(separator));	//得到头数据
            if (head) {	//是否将头数据写入到文件
                bufferedWriter.write(strT + System.getProperty("line.separator"));
            }
            int total = baseService.getTotlaBySql(queryRunner, sql).intValue();
            for(int i=1;i<=total/batchSize+1;i++){
                String pageSql2 = baseService.getPageBeanSqlOracle(sql, i, batchSize);
                List<Object[]> list = baseService.selectListArrayBySql(queryRunner, pageSql2);
                for (Object[] objects:list) {
                    String strLin ="";
                    for(int j=0;j<objects.length;j++){	//遍历一行数据
                        String objStr=objects[j]+"";
                        strLin+=objStr+separator;
                    }
                    if(strLin.contains("null")){
//                    	logger.info("数据中包含有null,将会被替换成空字符串:"+strLin);
                    }
                    strLin=strLin.replace("null", "");
                    strLin = strLin.substring(0, strLin.length() - 1);
//                    strLin = strLin.replace(", ", separator);
                    strLin=strLin.substring(0, strLin.lastIndexOf(separator));	//去掉最后的序列列
                    bufferedWriter.write(strLin+System.getProperty("line.separator"));
                }
                bufferedWriter.flush();
            }
            bufferedWriter.flush();
            return strT;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException var23) {
                var23.printStackTrace();
            }
        }
        return strT;
    }

    public static void main(String[] args){
//        String sql="select * from t_icphj_temp";
        String sql="select * from t_dns_top300_doamin_url";
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddhhmmss");
//        File file=new File("e:/sqlFile_"+simpleDateFormat.format(new Date())+".csv");
        File file1=new File("/Users/l/develop/abc.csv");
//        getFileBysql(sql,file1,"|",true);
        String headStr = getFileBySql(sql, file1, "|%|", false, 5000);
        System.out.println(headStr);
    }
}
