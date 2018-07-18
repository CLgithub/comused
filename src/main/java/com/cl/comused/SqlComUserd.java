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

    /**
     * 根据csv文件，建表，并将文件数据插入表
     * @param conn 数据库链接
     * @param csvFile csv文件
     * @param coding 文件编码
     * @param separator csv文件分隔符
     * @param tableName 表名称
     * @param columns 表字段，默认取csv文件中头信息，若csv文件中头信息包含中文，则必须指定表字段，字段必须与csv文件列对应
     * @return 建表sql
     */
    public static String getTableByCSVFile(Connection conn, File csvFile, String coding, String separator, String tableName, String... columns){
        if(csvFile.exists()){
            PreparedStatement ps1=null;
            PreparedStatement ps2=null;
            String[] cs=null;
            BufferedReader bReader = null;
            String createSql=null;
            try {
                bReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), coding));
                //0 建表
                String headStr = bReader.readLine();
                String[] headSplit = headStr.split(separator, -1);
                String sql1="";
                if(columns.length!=0){
                    if(headSplit.length==columns.length){
                        cs=columns;
                    } else{
                    }
                }else{
                    cs=headSplit;
                }
                for(String column:cs){
                    sql1+=column+" varchar2(200), ";
                }
                sql1=sql1.substring(0,sql1.length()-2);
                createSql="create table "+tableName+" ( "+sql1+" )";
                ps1 = conn.prepareStatement(createSql);
                boolean execute = ps1.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    //1 插入数据
                    String sql2 = "";
                    for (int i = 0; i < cs.length; i++) {
                        sql2 += "?, ";
                    }
                    sql2 = sql2.substring(0, sql2.length() - 2);
                    String insertSql = "insert into " + tableName + " values(" + sql2 + ")";
                    ps2 = conn.prepareStatement(insertSql);
                    int count = 0;
                    int batchSize = 10000;
                    for (String strLine = bReader.readLine(); strLine != null; strLine = bReader.readLine()) {
                        String[] split = strLine.split(separator, -1);
                        for (int j = 0; j < cs.length; j++) {
                            System.out.println(split[j]);
                            ps2.setString(j + 1, split[j]);
                        }
                        ps2.addBatch();
                        if (++count % batchSize == 0) {
                            ps2.executeBatch();
                            ps2.clearBatch();
                        }
                    }
                    ps2.executeBatch();
                    ps2.clearBatch();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    try {
                        if(ps1!=null) ps1.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if(ps2!=null) ps2.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(bReader!=null) bReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return createSql;
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = JDBCUtilHikariCP.getConnection();
        File file = new File("/Users/l/develop/ultrapower/ultrapowerBin/30-GzCsvFileToTable/IDC-CDN-Cache--wangye20180713.csv");
        String[] cloumns={"time", "infoGroupNmae", "name", "indexByteTime", "pageLoadTime", "inout", "hostip", "address"};
        String str = getTableByCSVFile(connection, file, "gbk",",","t1234" ,cloumns);

    }
}
